package musinsa.service;

import musinsa.domain.product.dto.BrandUpsertRequest;
import musinsa.domain.product.dto.ProductUpsertRequest;

public interface AdminService {
  void upsertProduct(ProductUpsertRequest productUpsertRequest);
  void deleteProduct(Long productId);
  void upsertBrand(BrandUpsertRequest brandUpsertRequest);
  void deleteBrand(Long brandId);
}
