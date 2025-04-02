package musinsa.domain.product.dto;

import java.util.List;

public record CategoryPriceInfoDto(
    String category,
    List<BrandPrice> lowest,
    List<BrandPrice> highest
) {
  public record BrandPrice(
      String brand,
      int price
  ) {}
}