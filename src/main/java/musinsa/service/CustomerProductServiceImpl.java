package musinsa.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import musinsa.common.exception.defined.BusinessException;
import musinsa.common.model.ErrorCode;
import musinsa.domain.category.entity.Category;
import musinsa.domain.category.repository.CategoryRepository;
import musinsa.domain.product.dto.CategoryPriceInfoDto;
import musinsa.domain.product.dto.CategoryPriceInfoDto.BrandPrice;
import musinsa.domain.product.dto.LowestBrandProductDto;
import musinsa.domain.product.dto.LowestBrandProductDto.CategoryPrice;
import musinsa.domain.product.dto.LowestBrandProductDto.LowestInfo;
import musinsa.domain.product.dto.LowestCategoryProductDto;
import musinsa.domain.product.dto.LowestCategoryProductDto.CategoryBrand;
import musinsa.domain.product.entity.Product;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerProductServiceImpl implements CustomerProductService {

  private final ProductCategoryCacheService productCategoryCacheService;
  private final CategoryRepository categoryRepository;

  @Cacheable(value = "category_lowest_price_rank", sync = true) // sync = true, 캐시 스템피드 방지
  @Override
  public LowestCategoryProductDto getAllCategoryLowestProduct() {
    //1. 모든 카테고리 항목 조회
    List<Category> categories = categoryRepository.findAll();

    //2. 카테고리별 최저 상품 가격 조회 및 합계 계산(상품 정보)
    int totalPrice = 0;
    List<CategoryBrand> categoryBrandList = new ArrayList<>();
    for (Category category : categories) {
      List<Product> productList = productCategoryCacheService.getProductsByCategoryCache(category);

      if (productList.isEmpty()) {
        throw new BusinessException(ErrorCode.CATEGORY_PRODUCT_NOT_FOUND);
      }

      int lowestPrice = productList.get(0).getPrice(); //카테고리별 최저가

      // 2-1. 최저가에 해당하는 모든 브랜드 필터링
      List<BrandPrice> lowestBrandList = productList.stream()
          .filter(p -> p.getPrice() == lowestPrice)
          .map(p -> new BrandPrice(p.getBrand().getName(), p.getPrice()))
          .toList();
      categoryBrandList.add(new CategoryBrand(category.getName(), lowestBrandList));
      totalPrice += lowestPrice;
    }

    return new LowestCategoryProductDto(categoryBrandList, totalPrice);
  }

  @Cacheable(value = "brand_lowest_price_rank", sync = true)
  @Override
  public List<LowestBrandProductDto> getBrandLowestProductCache() {
    //1. 모든 카테고리 항목 조회
    List<Category> categoryList = categoryRepository.findAll();

    //2. HashMap<브랜드, HashMap<카테고리, 가격>>의 형태로 데이터 정제
    Map<String, Map<String, Integer>> brandCategoryPriceMap = new HashMap<>();
    categoryList.forEach(category -> {
      List<Product> productList = productCategoryCacheService.getProductsByCategoryCache(category);

      productList.forEach(product -> {
        String brand = product.getBrand().getName();
        String categoryName = category.getName();
        int price = product.getPrice();

        brandCategoryPriceMap
            .computeIfAbsent(brand, k -> new HashMap<>())
            .merge(categoryName, price, Math::min);  // 최저가만 유지
      });
    });

    // 3. 브랜드마다 모든 카테고리 영역에 최저가의 총합 기록
    int lowestTotal = Integer.MAX_VALUE;
    Map<String, Integer> brandTotalPriceMap = new HashMap<>();

    for (Entry<String, Map<String, Integer>> entry : brandCategoryPriceMap.entrySet()) {
      String brand = entry.getKey();
      Map<String, Integer> categoryInfo = entry.getValue();

      // 브랜드마다 무조건 모든 카테고리의 상품이 반드시 1개씩 존재해야한다.
      if(categoryInfo.keySet().size() != categoryList.size()) continue;

      int totalPrice = categoryInfo.values().stream().mapToInt(Integer::intValue).sum();
      brandTotalPriceMap.put(brand, totalPrice);

      // 최저가 갱신
      if (totalPrice < lowestTotal) {
        lowestTotal = totalPrice;
      }
    }

    // 4. 총합 비교 (최저가가 동일한 브랜드가 여러개 존재할 수 있음)
    List<LowestBrandProductDto> lowestBrandProductDtoList = new ArrayList<>();
    for (Entry<String, Integer> entry : brandTotalPriceMap.entrySet()) {
      String brand = entry.getKey();
      if (entry.getValue() == lowestTotal) {
        lowestBrandProductDtoList.add(
            toLowestBrandProductDto(brand, brandCategoryPriceMap.get(brand), entry.getValue()));
      }
    }

    if (lowestBrandProductDtoList.isEmpty()) {
      throw new BusinessException(ErrorCode.CATEGORY_BRAND_TOTAL_CALCULATION_FAILED);
    }

    return lowestBrandProductDtoList;
  }

  @Override
  public CategoryPriceInfoDto getCategoryPriceInfo(String categoryName) {
    // 1. 카테고리 존재 확인
    Category category = categoryRepository.findByCategory(categoryName.toUpperCase())
        .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_CATEGORY));

    // 2. 카테고리별 상품 가격정보(오름차순 정렬 O)를 캐시 조회
    List<Product> productList = productCategoryCacheService.getProductsByCategoryCache(category);

    int lowestPrice = productList.get(0).getPrice();
    int highestPrice = productList.get(productList.size()-1).getPrice();
    List<BrandPrice> lowest = new ArrayList<>();
    List<BrandPrice> highest = new ArrayList<>();

    // 3. 카테고리 내에서 가격 최저 ~ 최대 값을 배열에 추가(같은 가격 고려)
    productList.forEach(product -> {
      if(product.getPrice() == lowestPrice) {
        lowest.add(new BrandPrice(product.getBrand().getName(), product.getPrice()));
      }
      if(product.getPrice() == highestPrice) {
        highest.add(new BrandPrice(product.getBrand().getName(), product.getPrice()));
      }
    });

    return new CategoryPriceInfoDto(category.getName(), lowest, highest);
  }

  private LowestBrandProductDto toLowestBrandProductDto(String brand, Map<String, Integer> categoryPriceMap, int total) {
    List<CategoryPrice> categoryPrices = categoryPriceMap.entrySet().stream()
        .map(entry -> new CategoryPrice(entry.getKey(), entry.getValue()))
        .toList();

    LowestInfo lowestInfo = new LowestInfo(
        brand,
        categoryPrices,
        total
    );

    return new LowestBrandProductDto(lowestInfo);
  }
}
