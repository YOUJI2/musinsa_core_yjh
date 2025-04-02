package musinsa.service.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import musinsa.common.exception.defined.BusinessException;
import musinsa.common.model.ErrorCode;
import musinsa.common.util.cache.CacheEvictionManager;
import musinsa.domain.brand.entity.Brand;
import musinsa.domain.brand.repository.BrandRepository;
import musinsa.domain.category.entity.Category;
import musinsa.domain.category.repository.CategoryRepository;
import musinsa.domain.product.dto.BrandUpsertRequest;
import musinsa.domain.product.dto.ProductUpsertRequest;
import musinsa.domain.product.entity.Product;
import musinsa.domain.product.repository.ProductRepository;
import musinsa.service.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminServiceImplTest {

  @Mock
  private BrandRepository brandRepository;

  @Mock
  private CategoryRepository categoryRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private CacheEvictionManager cacheEvictionManager;

  @InjectMocks
  private AdminServiceImpl adminService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(adminService, "CHUNK_SIZE", 1000);
  }

  @Test
  void 상품_생성_성공() {
    // given
    ProductUpsertRequest request = new ProductUpsertRequest(null, "b_top", 1000, "b", "TOP");
    Brand brand = Brand.create("B");
    Category category = new Category(1L, "TOP");

    // when
    when(brandRepository.findByBrandName("b")).thenReturn(Optional.of(brand));
    when(categoryRepository.findByCategory("TOP")).thenReturn(Optional.of(category));
    when(productRepository.isLowestPriceInCategory(category, 1000)).thenReturn(true);
    when(productRepository.isHighestPriceInCategory(category, 1000)).thenReturn(false);
    when(productRepository.existsByName("b_top")).thenReturn(false);

    adminService.upsertProduct(request);

    // verify
    verify(productRepository).save(any(Product.class));
    verify(cacheEvictionManager).evictCachesByCondition(true, false, category.getName().toLowerCase());
  }

  @Test
  void 브랜드_수정_중복_실패() {
    BrandUpsertRequest request = new BrandUpsertRequest(1L, "b");
    when(brandRepository.existsByNameAndIdNot("b", 1L)).thenReturn(true);

    BusinessException thrown = assertThrows(BusinessException.class, () -> adminService.upsertBrand(request));

    // then
    assertEquals(ErrorCode.DUPLICATE_BRAND_NAME, thrown.getErrorCode());
  }

  @Test
  void 상품_삭제_성공() {
    Category category = new Category(1L, "TOP");
    Brand brand = Brand.create("B");
    Product product = Product.create("item", brand, category, 500);
    when(productRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(product));
    when(productRepository.isLowestPriceInCategory(category, 500)).thenReturn(true);
    when(productRepository.isHighestPriceInCategory(category, 500)).thenReturn(true);

    adminService.deleteProduct(1L);

    verify(productRepository).delete(product);
    verify(cacheEvictionManager).evictCachesByCondition(true, true, category.getName().toLowerCase());
  }

  @Test
  void 브랜드_삭제_성공() {
    Brand brand = Brand.create("B");
    when(brandRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(brand));
    when(productRepository.findProductIdListByBrand(brand)).thenReturn(List.of(1L, 2L));

    adminService.deleteBrand(1L);

    verify(productRepository, times(1)).deleteByProductIdList(any());
    verify(brandRepository).delete(brand);
    verify(cacheEvictionManager).evictAllCaches();
  }
}