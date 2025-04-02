package musinsa.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import musinsa.common.exception.defined.BusinessException;
import musinsa.common.model.ErrorCode;
import musinsa.domain.category.entity.Category;
import musinsa.domain.product.entity.Product;
import musinsa.domain.product.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCategoryCacheService {

  private final ProductRepository productRepository;

  @Cacheable(cacheResolver = "categoryCacheResolver", key = "#category.name.toLowerCase()", sync = true) // 캐시 스탬피드 방지
  public List<Product> getProductsByCategoryCache(Category category) {
    List<Product> productList = productRepository.findAllByCategoryOrderByPrice(
        category);

    if (productList.isEmpty()) {
      throw new BusinessException(ErrorCode.CATEGORY_PRODUCT_NOT_FOUND);
    }

    return productList;
  }
}
