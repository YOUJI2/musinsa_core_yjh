package musinsa.common.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  //400번 에러
  INVALID_INPUT_CATEGORY("40001", "잘못된 요청 : 파라미터 에러", HttpStatus.BAD_REQUEST, "존재하지 않은 카테고리이름 입니다."),
  INVALID_CATEGORY_TYPE("40002", "잘못된 요청 : 타입 에러", HttpStatus.BAD_REQUEST, "잘못된 타입입니다."),

  //404번 에러
  CATEGORY_PRODUCT_NOT_FOUND("40401", "비지니스 에러 : 조회 데이터 오류", HttpStatus.NOT_FOUND, "해당 카테고리에 대한 상품이 존재하지 않습니다."),
  BRAND_NOT_FOUND("40402", "비지니스 에러 : 조회 데이터 오류", HttpStatus.NOT_FOUND, "해당 브랜드가 존재하지 않습니다."),
  CATEGORY_NOT_FOUND("40403", "비지니스 에러 : 조회 데이터 오류", HttpStatus.NOT_FOUND, "해당 카테고리가 존재하지 않습니다."),
  PRODUCT_NOT_FOUND("40404", "비지니스 에러 : 조회 데이터 오류", HttpStatus.NOT_FOUND, "해당 상품은 존재하지 않습니다."),

  //409번 에러
  DUPLICATE_PRODUCT_NAME("40901", "비지니스 에러 : 이름 중복", HttpStatus.CONFLICT, "이미 존재하는 상품 이름입니다."),
  DUPLICATE_BRAND_NAME("40902", "비지니스 에러 : 이름 중복", HttpStatus.CONFLICT, "이미 존재하는 브랜드 이름입니다."),

  //422번 에러
  CATEGORY_BRAND_TOTAL_CALCULATION_FAILED("42201", "비지니스 에러 : 총합 계산 불가", HttpStatus.UNPROCESSABLE_ENTITY, "최저가 브랜드를 계산할 수 없습니다. (카테고리/상품/브랜드 데이터 누락 가능성)"),

  //500번대 에러
  FAIL_RESPONSE_UTIL_ERROR("50001", "서버 에러", HttpStatus.INTERNAL_SERVER_ERROR, "ResponseUtils 인스턴스화는 금지입니다."),
  CACHE_NOT_FOUND_ERROR("50002", "서버 에러", HttpStatus.INTERNAL_SERVER_ERROR, "서버설정시 캐시가 누락되었습니다."),
  INVALID_CACHE_MANAGER_TYPE("50003", "서버 에러", HttpStatus.INTERNAL_SERVER_ERROR, "지원하지않는 캐시 타입입니다.");

  private final String code;
  private final String title;
  private final HttpStatus status;
  private final String detailMessage;

  @Override
  public String toString() {
    return "ErrorCode{" +
        "code='" + code + '\'' +
        ", title='" + title + '\'' +
        ", status=" + status +
        ", detailMessage='" + detailMessage + '\'' +
        '}';
  }
}
