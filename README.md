# 무신사 코어 백엔드 과제

## 📎 기술 스택
- JDK 21
- Spring Boot: ver 3.4.4
- DB: H2
- JPA
- Gradle

## 📌 구현 범위

### 1. 고객용 API
- **카테고리별 최저가격 브랜드 및 총액 조회 API**
- **단일 브랜드의 전체 카테고리 상품 구매 시 최저가격 브랜드 및 총액 계산 조회 API**
- **특정 카테고리 내 최저가격 및 최고가격 브랜드 확인 조회 API**

### 2. 어드민 API
- **새로운 상품 등록 및 수정 API**
- **상품 삭제 API**
- **브랜드 등록 및 수정 API**
- **브랜드 삭제 API**

## 💻 설계
 
- **테이블 구조 및 엔티티 구성**
  - [Brand]와 [Product]를 기준으로, Product는 하나의 Category에 속하도록 구성
    ```
    [Brand] 1 --- * [Product] * --- 1 [Category]
    ```
  - JPA 기반 엔티티 구성 및 연관관계 맵핑 활용
    ``` yaml
    spring:
      jpa:
        hibernate:
          ddl-auto: create
        defer-datasource-initialization: true
      sql:
        init:
          mode: always
          data-locations: classpath:/init/musinsa-data.sql   
    ```
    - 애플리케이션 시작시 테이블 형성, 데이터는 JPA로 테이블 형성 후 초기화 진행
    - sql 스크립트로 인덱스 형성 및 초기 데이터 로드

- **조회 성능 개선을 위한 Local Cache 사용**
  - 고객 API에서 특정 브랜드, 카테고리 등의 최저가격 정보를 캐싱하여, 다수의 요청에도 동일한 데이터를 빠르게 반환
  - 단일 인스턴스를 가정하고 Local Cache 적용 (Caffine Cache의 sync 옵션으로 Cache 스탬피드 방지)
    ``` yaml
    spring:
      cache:
        type: caffeine
    ```
    - Spring Cache 추상화를 사용하며 구현체로 Caffeine Cache를 사용

- **예외 처리 모듈화**
  - BusinessException을 구현하여 발생 가능한 예외를 Enum 기반의 ErrorCode로 정의
  - 클라이언트에는 1차적인 에러 메시지와 커스텀 에러 코드를 전달하고, 상세한 에러 내용은 문서화

- **응답 객체 모듈화**
  - 별도의 ResponseObject를 활용해 에러와 반환 데이터를 공통 구조로 처리

- **비즈니스 로직에 대한 단위 테스트 및 통합 테스트 진행**
  - 동시 요청에 대한 동시성 처리 테스트 진행

## 🛠️ 빌드 및 실행

### Gradle 빌드 및 실행
- **빌드**
  ```bash
  ./gradlew bootJar
  ```
- **실행**
  ```
  java -jar build/libs/musinsa-yjh-1.0.0.jar
  ```
- **테스트 실행**
  ```
  ./gradlew test
  ```

## 🧐 고민했던 부분

### 1. Cache 동시성 처리
- **상황**  
  조회 API의 경우, 캐시가 만료된 후 다수의 요청이 동시에 캐시를 갱신하려 할 때 문제가 발생할 수 있다
- **동시성 처리 방법**  
  Caffeine Cache의 `sync=true` 옵션을 활용하여 동기화 처리 적용 (단일 인스턴스 환경에서는 대부분 해결 가능)

### 2. 시스템 Cache 구조 설계 (동적 Cache와 정적 Cache)
고객의 API 요청에 대해 빠르고 일관된 응답을 제공하기 위해 두 단계의 캐시 전략을 적용허였고 이 구조는 크게 두 개의 서비스 레이어로 구성
- **정적 Cache (Outer Service)**
  - 역할
    - "카테고리별 최저가격 브랜드 조회 및 총액 조회 API"와 "단일 브랜드의 전체 카테고리 상품 구매 시 최저가격 브랜드 및 총액 계산 조회 API"의 2개의 캐시로 구성
    - 위 2개 API 요청시 정적 Cache를 확인 후 데이터 처리
  - 동작
    - 위 2개의 API요청을 처리하며 Cache가 없으면 내부 로직을 수행하게 되는데, 이때 동적 Cache로직이 포함된 다른 Service를 호출하여 데이터를 가공
- **동적 Cache (Inner Service)**
  - 역할
    - 카테고리수에 따른 상품의 최저~최대 가격 데이터에 대한 Cache 정보
    - 정적 Cache에 의해 호출되거나 "특정 카테고리의 최저가,최고가 상품정보 조회API"요청시 수행되어 데이터를 처리
  - 동작
    - 카테고리 종류에 따라 ApplicationContext 및 의존성 주입이 완료된 후 생성되어 할당
    - 카테고리의 경우 추후 변경사항이 있거나 확장될 있는 가정하에 코드레벨에서 유지보수를 최소화 하고자 DB에 의존하도록 설계
  -  **Client 시스템 흐름도**
    - ![Image](https://github.com/user-attachments/assets/d440e9f0-56d2-468d-93e7-fd5de1c1af84)

### 3. 어드민에 의한 상품/브랜드 생성, 수정, 삭제 시 Cache 갱신 여부
- **상품의 cache 경우**
  - 최저가 상품이면 관련 브랜드와 카테고리 캐시 모두 갱신한다. 조회 결과가 달라지기 때문
  - 최고가 상품은 해당 카테고리(동적 Cache)만 갱신한다. 최저가를 보여주는 API에는 영향이 없다
- **브랜드의 cache 경우**
  - 신규 생성은 캐시에 영향을 주지 않기때문에 갱신하지 않는다
  - 수정/삭제 시에는 모든 관련 캐시를 갱신하는데 이는 상품의 정보 자체가 달라지기 때문에 관련된 캐시들을 갱신해주는 방향으로 고려
- **브랜드 삭제 시**
  - 브랜드를 삭제하는 경우 연관 상품 수가 대용량일 수도 있기 때문에 이를 메모리로 관리않기 위해 JPA로 처리하지 않고 chunk size로 조절하여 삭제를 진행

### 4. 어드민에서 상품 및 브랜드에 대한 동시성 처리
- **생성 시**
  - 이름에 unique 제약조건을 걸어 동시에 DB에 write 요청 시 예외 처리로 동시 접근 제어하도록 구현
- **수정 및 삭제 시**
  - 비관적 락(Pessimistic Lock)을 사용하여 id를 통한 객체 조회시 특정 row의 대한 접근을 제어하도록 구현
  - @Lock(LockModeType.PESSIMISTIC_WRITE) 활용
  - **Admin 시스템 흐름도**
    - ![Image](https://github.com/user-attachments/assets/4ec6dac3-751d-4321-9eec-4d6bcbfa2a91)

### 5. 예외처리 방식에 대한 고민
- **공통된 에러코드 형식 사용**
  - ErrorCode라는 별도의 Enum 객체로 관리하여 HttpStatus와 Client와의 약속된 에러 코드를 정의 및 예외의 대한 제목과 상세 메시지로 관리
  - 실제 client에게 내려주는 에러는 예외의 제목 부분과 에러 코드만 제공하고 에러 코드로 자세한 예외 메시지를 확인하도록 설계
  - 보안을 고려한 에러처리 방식

 
