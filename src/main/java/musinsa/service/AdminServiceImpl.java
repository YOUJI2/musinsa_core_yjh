package musinsa.service;

import io.micrometer.common.lang.Nullable;
import java.util.List;
import lombok.RequiredArgsConstructor;
import musinsa.common.exception.defined.BusinessException;
import musinsa.common.model.ErrorCode;
import musinsa.common.util.CollectionUtils;
import musinsa.common.util.cache.CacheEvictionManager;
import musinsa.domain.brand.entity.Brand;
import musinsa.domain.brand.repository.BrandRepository;
import musinsa.domain.category.entity.Category;
import musinsa.domain.category.repository.CategoryRepository;
import musinsa.domain.product.dto.BrandUpsertRequest;
import musinsa.domain.product.dto.ProductUpsertRequest;
import musinsa.domain.product.entity.Product;
import musinsa.domain.product.repository.ProductRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

  private final CacheEvictionManager cacheEvictionManager;

  private final BrandRepository brandRepository;
  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;

  @Value("${delete.chunk.size}")
  private int CHUNK_SIZE;

  @Transactional
  @Override
  public void upsertProduct(ProductUpsertRequest productUpsertRequest) {
    // 1. 닉네임 중복 확인
    if (isDuplicateName(productUpsertRequest.name(), productUpsertRequest.productId())) {
      throw new BusinessException(ErrorCode.DUPLICATE_PRODUCT_NAME);
    }

    // 2. 브랜드, 카테고리 조회
    Brand brand = findBrand(productUpsertRequest.brand());
    Category category = findCategory(productUpsertRequest.category());

    // 3. 변경 및 생성하려는 가격이 최저가인지 확인
    boolean isLowestPrice = productRepository.isLowestPriceInCategory(category, productUpsertRequest.price());
    boolean isHighestPrice = productRepository.isHighestPriceInCategory(category, productUpsertRequest.price());

    // 4. 상품 id값으로 생성 or 수정 분기
    Product product;
    boolean needFullCacheEviction = false;
    if(productUpsertRequest.productId() != null && productUpsertRequest.productId() != 0L) {
      // 비관적락으로 상품 조회
      product = productRepository.findByIdForUpdate(productUpsertRequest.productId())
          .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
      product.updateProductInfo(brand, category, productUpsertRequest.price(),
          productUpsertRequest.name());

      boolean isBrandChanged = !StringUtils.equals(productUpsertRequest.brand(), brand.getName());
      boolean isCategoryChanged = !StringUtils.equals(productUpsertRequest.category(), category.getName());
      needFullCacheEviction = isBrandChanged || isCategoryChanged;

    } else {
      product = Product.create(productUpsertRequest.name(), brand, category, productUpsertRequest.price());
    }

    // 5. 상품 upsert
    productRepository.save(product);

    // 6. 캐시 초기화 진행
    if(needFullCacheEviction) {
      cacheEvictionManager.evictAllCaches();
    } else {
      cacheEvictionManager.evictCachesByCondition(isLowestPrice, isHighestPrice, category.getName().toLowerCase());
    }
  }

  @Transactional
  @Override
  public void deleteProduct(Long productId) {
    // 1. 상품 조회 및 row-level lock
    Product product = productRepository.findByIdForUpdate(productId)
        .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

    Category category = product.getCategory();
    int price = product.getPrice();

    // 2. 최저가, 최고가 확인 (캐시 갱신 판단)
    boolean isLowest = productRepository.isLowestPriceInCategory(category, price);
    boolean isHighest = productRepository.isHighestPriceInCategory(category, price);

    // 3. 상품 삭제
    productRepository.delete(product);

    // 4. 캐시 무효화
    cacheEvictionManager.evictCachesByCondition(isLowest, isHighest, category.getName().toLowerCase());
  }

  @Transactional
  @Override
  public void upsertBrand(BrandUpsertRequest brandUpsertRequest) {
    // 1. 브랜드 이름 중복 확인
    if(isDuplicateBrandName(brandUpsertRequest.brand(), brandUpsertRequest.brandId())) {
      throw new BusinessException(ErrorCode.DUPLICATE_BRAND_NAME);
    }

    // 2. 브랜드 id값으로 생성 or 수정 분기
    Brand brand;
    if (brandUpsertRequest.brandId() != null && brandUpsertRequest.brandId() != 0L) {
      // 비관적 락으로 브랜드 조회
      brand = brandRepository.findByIdForUpdate(brandUpsertRequest.brandId())
          .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));

      // 이름이 변경된 경우에만 DB 업데이트 후 캐시 초기화
      if (!brand.getName().equals(brandUpsertRequest.brand())) {
        brand.updateBrandName(brandUpsertRequest.brand());
        brandRepository.save(brand);
        cacheEvictionManager.evictAllCaches(); // 전체 캐시 초기화
      }
    } else {
      brand = Brand.create(brandUpsertRequest.brand());
      brandRepository.save(brand);  // 브랜드 등록
    }
  }

  @Transactional
  @Override
  public void deleteBrand(Long brandId) {
    // 1. 비관적 락으로 브랜드 조회
    Brand brand = brandRepository.findByIdForUpdate(brandId)
        .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));

    // 2. 삭제할 상품 ID 리스트 조회
    List<Long> productIdList = productRepository.findProductIdListByBrand(brand);

    // 2. 벌크 삭제 (chunk size로 파티셔닝 구성 후 삭제)
    List<List<Long>> partitionList = CollectionUtils.partitionList(productIdList, CHUNK_SIZE);
    for(int i=0;i<partitionList.size();i++) {
      productRepository.deleteByProductIdList(partitionList.get(i));
    }

    // 3. 브랜드 삭제
    brandRepository.delete(brand);

    // 4. 전체 캐시 초기화
    cacheEvictionManager.evictAllCaches();
  }

  private Brand findBrand(String name) {
    return brandRepository.findByBrandName(name)
        .orElseThrow(() -> new BusinessException(ErrorCode.BRAND_NOT_FOUND));
  }

  private Category findCategory(String name) {
    return categoryRepository.findByCategory(name)
        .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
  }

  private boolean isDuplicateName(String name, @Nullable Long id) {
    return id == null
        ? productRepository.existsByName(name)
        : productRepository.existsByNameAndIdNot(name, id);
  }

  private boolean isDuplicateBrandName(String name, @Nullable Long id) {
    return id == null
        ? brandRepository.existsByName(name)
        : brandRepository.existsByNameAndIdNot(name, id);
  }
}
