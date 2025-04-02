package musinsa.service.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import musinsa.common.exception.defined.BusinessException;
import musinsa.common.model.ErrorCode;
import musinsa.domain.brand.entity.Brand;
import musinsa.domain.category.entity.Category;
import musinsa.domain.category.repository.CategoryRepository;
import musinsa.domain.product.dto.CategoryPriceInfoDto;
import musinsa.domain.product.dto.LowestBrandProductDto;
import musinsa.domain.product.dto.LowestCategoryProductDto;
import musinsa.domain.product.dto.LowestCategoryProductDto.CategoryBrand;
import musinsa.domain.product.entity.Product;
import musinsa.service.CustomerProductServiceImpl;
import musinsa.service.ProductCategoryCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomerProductServiceImplTest {

  @Mock
  private ProductCategoryCacheService cacheService;

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CustomerProductServiceImpl customerProductService;

  @Test
  void 카테고리별_최저브랜드_및_가격조회_정상동작() {
    // given
    Category top = new Category(1L, "TOP");
    Category outer = new Category(2L, "OUTER");
    List<Category> categories = List.of(top, outer);

    Brand brandA = Brand.create(1L, "A");
    Brand brandB = Brand.create(2L, "B");

    List<Product> topProducts = List.of(
        Product.create("top_A", brandA, top, 10000),
        Product.create("top_B", brandB, top, 11000)
    );
    List<Product> outerProducts = List.of(
        Product.create("outer_B", brandB, outer, 9000),
        Product.create("outer_A", brandA, outer, 9500)
    );

    // when
    when(categoryRepository.findAll()).thenReturn(categories);
    when(cacheService.getProductsByCategoryCache(top)).thenReturn(topProducts);
    when(cacheService.getProductsByCategoryCache(outer)).thenReturn(outerProducts);
    LowestCategoryProductDto result = customerProductService.getAllCategoryLowestProduct();

    // then
    assertThat(result.total()).isEqualTo(10000 + 9000);
    assertThat(result.categories()).hasSize(2);

    CategoryBrand topResult = result.categories().get(0);
    assertThat(topResult.category()).isEqualTo("TOP");
    assertThat(topResult.brandPrices()).hasSize(1);
    assertThat(topResult.brandPrices().get(0).brand()).isEqualTo("A");
  }

  @Test
  void 브랜드별_최저가_카테고리별상품_조합_조회_정상동작() {
    // given
    Category top = new Category(1L, "TOP");
    Category pants = new Category(2L, "PANTS");

    Brand brandA = Brand.create(1L, "A");
    Brand brandB = Brand.create(2L, "B");

    Product aTop = Product.create( "A_TOP", brandA, top, 1000);
    Product bTop = Product.create( "B_TOP", brandB, top, 1500);
    Product aPants = Product.create( "A_PANTS", brandA, pants, 1100);
    Product bPants = Product.create( "B_PANTS", brandB, pants, 900);

    when(categoryRepository.findAll()).thenReturn(List.of(top, pants));
    when(cacheService.getProductsByCategoryCache(top))
        .thenReturn(List.of(aTop, bTop));
    when(cacheService.getProductsByCategoryCache(pants))
        .thenReturn(List.of(aPants, bPants));

    // when
    List<LowestBrandProductDto> result = customerProductService.getBrandLowestProductCache();

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).lowest().brand()).isEqualTo("A");
    assertThat(result.get(0).lowest().total()).isEqualTo(2100);
  }

  @Test
  void 브랜드별_최저가_총합_없음_예외처리() {
    // given
    when(categoryRepository.findAll()).thenReturn(List.of());

    // then
    BusinessException thrown = assertThrows(BusinessException.class,
        () -> customerProductService.getBrandLowestProductCache());
    assertEquals(ErrorCode.CATEGORY_BRAND_TOTAL_CALCULATION_FAILED, thrown.getErrorCode());
  }

  @Test
  void 카테고리_가격정보조회_정상동작() {
    // given
    Category top = new Category(1L, "TOP");
    Brand brandA = Brand.create(1L, "A");
    Brand brandB = Brand.create(2L, "B");
    Brand brandC = Brand.create(1L, "C");
    Brand brandD = Brand.create(2L, "D");

    Product p1 = Product.create( "A_TOP", brandA, top, 10000);
    Product p2 = Product.create( "B_TOP", brandB, top, 10000);
    Product p3 = Product.create( "C_TOP", brandC, top, 11000);
    Product p4 = Product.create( "D_TOP", brandD, top, 12000);

    given(categoryRepository.findByCategory("TOP")).willReturn(java.util.Optional.of(top));
    given(cacheService.getProductsByCategoryCache(top))
        .willReturn(List.of(p1, p2, p3, p4));

    CategoryPriceInfoDto dto = customerProductService.getCategoryPriceInfo("TOP");

    // then
    assertThat(dto.category()).isEqualTo("TOP");
    assertThat(dto.lowest()).hasSize(2);
    assertThat(dto.highest()).hasSize(1);
    assertThat(dto.lowest().get(0).price()).isEqualTo(10000);
    assertThat(dto.highest().get(0).price()).isEqualTo(12000);
  }

  @Test
  void 존재하지_않는_카테고리_가격정보조회_예외처리() {
    // given
    given(categoryRepository.findByCategory("INVALID")).willReturn(java.util.Optional.empty());

    // then
    BusinessException thrown = assertThrows(BusinessException.class,
        () -> customerProductService.getCategoryPriceInfo("INVALID"));
    assertEquals(ErrorCode.INVALID_INPUT_CATEGORY, thrown.getErrorCode());
  }

  @Test
  void 존재하지_않는_카테고리_예외처리() {
    // when
    when(categoryRepository.findByCategory("INVALID"))
        .thenReturn(Optional.empty());

    // then
    assertThrows(BusinessException.class, () -> customerProductService.getCategoryPriceInfo("INVALID"));
  }

  @Test
  void 특정_카테고리의_상품이_없을경우_예외처리() {
    // given
    Category category = new Category(1L, "TOP");

    // when
    when(categoryRepository.findAll()).thenReturn(List.of(category));
    when(cacheService.getProductsByCategoryCache(category)).thenReturn(Collections.emptyList());

    BusinessException thrown = assertThrows(BusinessException.class, () ->
        customerProductService.getAllCategoryLowestProduct()
    );

    // then
    assertEquals(ErrorCode.CATEGORY_PRODUCT_NOT_FOUND, thrown.getErrorCode());
  }
}
