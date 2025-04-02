package musinsa.domain.product.dto;

import java.util.List;
import musinsa.domain.product.dto.CategoryPriceInfoDto.BrandPrice;

public record LowestCategoryProductDto(
    List<CategoryBrand> categories,
    int total
) {

  public record CategoryBrand(
      String category,
      List<BrandPrice> brandPrices
  ) {}
}