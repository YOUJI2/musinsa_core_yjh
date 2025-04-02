package musinsa.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import musinsa.common.model.ResponseObject;
import musinsa.common.util.ResponseUtils;
import musinsa.domain.product.dto.CategoryPriceInfoDto;
import musinsa.domain.product.dto.LowestBrandProductDto;
import musinsa.domain.product.dto.LowestCategoryProductDto;
import musinsa.service.CustomerProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "고객용 API", description = "카테고리별 최저가격 및 총액 조회, 단일 브랜드의 전체 카테고리 최저가격 및 총액 계산, 특정 카테고리 내 최저/최고가격 브랜드와 상품 가격 조회 기능을 제공합니다.")
@RequestMapping(value = "/api/customer", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerProductController {

  private final CustomerProductService customerProductService;

  /**
   *  구현 1) 카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API
   */
  @Operation(summary = "카테고리별 최저가 상품 리스트", description = "카테고리별 최저 가격 브랜드 및 총 합을 조회")
  @GetMapping("/categories/lowest/info")
  public ResponseEntity<ResponseObject<LowestCategoryProductDto>> getAllCategoryLowestProduct() {
    LowestCategoryProductDto allCategoryLowestProduct = customerProductService.getAllCategoryLowestProduct();
    return ResponseUtils.createResponseEntity(allCategoryLowestProduct, HttpStatus.OK);
  }

  /**
   *  구현 2) 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액 조회 API
   */
  @Operation(summary = "최저가 단일 브랜드 세트 상품", description = "단일 브랜드의 전체 카테고리 상품 구매 시 최저가격 브랜드 및 총액 계산 조회")
  @GetMapping("/brands/lowest/info")
  public ResponseEntity<ResponseObject<List<LowestBrandProductDto>>> getBrandLowestProduct() {
    List<LowestBrandProductDto> brandLowestProduct = customerProductService.getBrandLowestProductCache();
    return ResponseUtils.createResponseEntity(brandLowestProduct, HttpStatus.OK);
  }

  /**
   *  구현 3) 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API
   *  @PathVariable categoryName
   */
  @Operation(summary = "특정 카테고리의 최저가,최고가 상품 정보", description = "특정 카테고리 내 최저가격 및 최고가격 브랜드 확인 조회")
  @Parameter(name = "categoryName", description = "카테고리는 대문자로 입력해주세요(ex TOP, OUTER, PANTS, SNEAKERS, BAG, HAT, SOCKS, ACCESSORY)")
  @GetMapping("/categories/{categoryName}/price/info")
  public ResponseEntity<ResponseObject<CategoryPriceInfoDto>> getCategoryPriceInfo(@PathVariable String categoryName) {
    CategoryPriceInfoDto categoryPriceInfo = customerProductService.getCategoryPriceInfo(categoryName);
    return ResponseUtils.createResponseEntity(categoryPriceInfo, HttpStatus.OK);
  }
}
