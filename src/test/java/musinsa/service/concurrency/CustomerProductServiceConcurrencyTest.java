package musinsa.service.concurrency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import musinsa.common.model.CacheType;
import musinsa.domain.category.entity.Category;
import musinsa.domain.category.repository.CategoryRepository;
import musinsa.domain.product.dto.LowestBrandProductDto;
import musinsa.domain.product.dto.LowestCategoryProductDto;
import musinsa.domain.product.entity.Product;
import musinsa.service.CustomerProductService;
import musinsa.service.ProductCategoryCacheService;
import musinsa.util.CacheStatsAssertUtils;
import musinsa.util.ConcurrentTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomerProductServiceConcurrencyTest {
  @Autowired
  private CustomerProductService customerProductService;

  @Autowired
  private ProductCategoryCacheService productCategoryCacheService;

  @Autowired
  private CacheManager cacheManager;

  @Autowired
  private CategoryRepository categoryRepository;

  private int THREAD_COUNT = 10;

  @BeforeEach
  void setupCache() {
    String cacheName = "category_type_top";
    if (cacheManager.getCache(cacheName) == null) {
      ((SimpleCacheManager) cacheManager).setCaches(
          List.of(new CaffeineCache(
              cacheName,
              Caffeine.newBuilder()
                  .expireAfterWrite(600, TimeUnit.SECONDS)
                  .maximumSize(1000)
                  .build()
          ))
      );
      ((SimpleCacheManager) cacheManager).initializeCaches();
    }
  }

  @Test
  void 카테고리별_최저가격별_상품조회_캐시_동시성_테스트() throws InterruptedException {
    List<LowestCategoryProductDto> resultList = ConcurrentTestUtils.concurrentTaskWithResult(THREAD_COUNT,
        () -> customerProductService.getAllCategoryLowestProduct());

    Cache.ValueWrapper wrapper = Objects.requireNonNull(cacheManager
            .getCache(CacheType.CATEGORY_LOWEST_PRICE_RANK.getName()))
        .get(SimpleKey.EMPTY); // 기본 key

    assertNotNull(wrapper, "캐시 값이 존재해야 한다.");
    LowestCategoryProductDto cacheData = (LowestCategoryProductDto) wrapper.get();

    for (LowestCategoryProductDto result : resultList) {
      assertEquals(cacheData, result, "모든 결과는 동일한 캐시 데이터여야 한다.");
    }

    CacheStatsAssertUtils.assertCacheStats(
        cacheManager, CacheType.CATEGORY_LOWEST_PRICE_RANK.getName(), (long) (THREAD_COUNT - 1)); // 최소 캐시 히트 수
  }

  @Test
  void 브랜드별_최저가_상품_조회_캐시_동시성_테스트() throws InterruptedException {
    List<List<LowestBrandProductDto>> resultList = ConcurrentTestUtils.concurrentTaskWithResult(
        THREAD_COUNT,
        () -> customerProductService.getBrandLowestProductCache());

    Cache.ValueWrapper wrapper = Objects.requireNonNull(cacheManager
            .getCache(CacheType.BRAND_RANK.getName()))
        .get(SimpleKey.EMPTY); // 기본 key

    assertNotNull(wrapper, "캐시 값이 존재해야 한다.");
    List<LowestBrandProductDto> cacheData = (List<LowestBrandProductDto>) wrapper.get();

    for (List<LowestBrandProductDto> result : resultList) {
      assertEquals(cacheData, result, "모든 결과는 동일한 캐시 데이터여야 한다.");
    }

    CacheStatsAssertUtils.assertCacheStats(cacheManager, CacheType.BRAND_RANK.getName(), (long) (THREAD_COUNT - 1));
  }

  @Test
  void 카테고리별_가격_조회_캐시_동시성_테스트() throws InterruptedException {
    String categoryName = "TOP";
    Category category = categoryRepository.findByCategory(categoryName).get();
    String categoryCacheName = "category_type_top";

    List<List<Product>> resultList = ConcurrentTestUtils.concurrentTaskWithResult(
        THREAD_COUNT,
        () -> productCategoryCacheService.getProductsByCategoryCache(category));

    Cache.ValueWrapper wrapper = Objects.requireNonNull(cacheManager
            .getCache(categoryCacheName))
        .get(category.getName().toLowerCase());

    List<Product> cacheData = (List<Product>) wrapper.get();
    for (List<Product> result : resultList) {
      assertEquals(cacheData, result, "모든 결과는 동일한 캐시 데이터여야 한다.");
    }

    CacheStatsAssertUtils.assertCacheStats(cacheManager, categoryCacheName, (long) (THREAD_COUNT - 1));
  }
}
