package musinsa.service;

import java.util.List;
import musinsa.domain.product.dto.CategoryPriceInfoDto;
import musinsa.domain.product.dto.LowestBrandProductDto;
import musinsa.domain.product.dto.LowestCategoryProductDto;

public interface CustomerProductService {
  LowestCategoryProductDto getAllCategoryLowestProduct();
  List<LowestBrandProductDto> getBrandLowestProductCache();
  CategoryPriceInfoDto getCategoryPriceInfo(String categoryName);
}
