package musinsa.service.integration;

import musinsa.domain.product.dto.CategoryPriceInfoDto;
import musinsa.domain.product.dto.LowestBrandProductDto;
import musinsa.domain.product.dto.LowestCategoryProductDto;
import musinsa.service.CustomerProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CustomerProductServiceIntegrationTest {

  @Autowired
  private CustomerProductService customerProductService;

  @Test
  @DisplayName("1. 전체 카테고리별 최저가 브랜드 및 합계 조회")
  void 전체_카테고리별_최저가_브랜드조회() {
    // when
    LowestCategoryProductDto result = customerProductService.getAllCategoryLowestProduct();

    // then
    assertThat(result).isNotNull();
    assertThat(result.categories()).isNotEmpty();
    assertThat(result.total()).isGreaterThan(0);

    // verify
    assertThat(result.total()).isEqualTo(34100); // 카테고리별 최저가 상품 총합

  }

  @Test
  @DisplayName("2. 모든 브랜드 중 카테고리별 최저가 합산 최저 브랜드 조회")
  void 최저_합계_브랜드조회() {
    // when
    List<LowestBrandProductDto> result = customerProductService.getBrandLowestProductCache();

    // then
    assertThat(result).isNotEmpty();
    LowestBrandProductDto first = result.get(0);
    assertThat(first.lowest().brand()).isEqualTo("D"); // 최저 브랜드 정보
    assertThat(first.lowest().total()).isEqualTo(36100); // 총합
    assertThat(first.lowest().categories()).hasSize(8); // 8개 카테고리
  }

  @Test
  @DisplayName("3. 특정 카테고리의 최저가~최고가 브랜드 조회")
  void 카테고리별_최저가_최고가_조회() {
    // given
    String categoryName = "TOP";

    // when
    CategoryPriceInfoDto result = customerProductService.getCategoryPriceInfo(categoryName);

    // then
    assertThat(result).isNotNull();
    assertThat(result.category()).isEqualToIgnoringCase(categoryName);
    assertThat(result.lowest()).isNotEmpty();
    assertThat(result.highest()).isNotEmpty();

    int minPrice = result.lowest().get(0).price();
    int maxPrice = result.highest().get(0).price();
    assertThat(minPrice).isEqualTo(10000);
    assertThat(maxPrice).isEqualTo(11400);
  }
}