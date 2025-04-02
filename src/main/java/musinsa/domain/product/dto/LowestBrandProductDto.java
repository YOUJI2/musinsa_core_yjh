package musinsa.domain.product.dto;

import java.util.List;

public record LowestBrandProductDto(
    LowestInfo lowest
) {
  public record LowestInfo(
      String brand,
      List<CategoryPrice> categories,
      int total
  ) {}

  public record CategoryPrice(
      String category,
      int price
  ) {}
}