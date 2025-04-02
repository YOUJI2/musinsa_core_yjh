package musinsa.common.model;

import lombok.Getter;

@Getter
public enum CacheType {

  CATEGORY_LOWEST_PRICE_RANK(10, "category_lowest_price_rank", 10 * 60, 10000), //카테고리별 저가~고가 브랜드 리스트 정보
  BRAND_RANK(10, "brand_lowest_price_rank", 10 * 60, 10000); // 브랜드별 최저가 상품 및 코디 합계 정보

  CacheType(int initialCapacity, String name, int expiredTime, int maxSize) {
    this.initialCapacity = initialCapacity;
    this.name = name;
    this.expiredTime = expiredTime;
    this.maxSize = maxSize;
  }

  private int initialCapacity;
  private String name;
  private int expiredTime;
  private int maxSize;
}
