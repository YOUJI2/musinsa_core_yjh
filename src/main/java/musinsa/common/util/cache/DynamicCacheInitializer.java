package musinsa.common.util.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import musinsa.common.exception.defined.BusinessException;
import musinsa.common.model.ErrorCode;
import musinsa.domain.category.entity.Category;
import musinsa.domain.category.repository.CategoryRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DynamicCacheInitializer implements ApplicationListener<ApplicationReadyEvent> {

  private final CacheManager cacheManager;
  private final CategoryRepository categoryRepository;

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    List<Category> categories = categoryRepository.findAll();

    if (!(cacheManager instanceof SimpleCacheManager cache)) {
      throw new BusinessException(ErrorCode.INVALID_CACHE_MANAGER_TYPE);
    }

    // 카테고리별 동적 캐시 생성
    List<CaffeineCache> dynamicCaches = new ArrayList<>();
    for (Category category : categories) {
      dynamicCaches.add(new CaffeineCache(
          "category_type_" + category.getName().toLowerCase(),
          Caffeine.newBuilder()
              .recordStats()
              .initialCapacity(10)
              .expireAfterWrite(10 * 60, TimeUnit.SECONDS)
              .maximumSize(10000).build()
      ));
    }

    List<CaffeineCache> combineCacheList = new ArrayList<>(cache.getCacheNames().stream()
        .map(name -> (CaffeineCache) cache.getCache(name))
        .toList());
    combineCacheList.addAll(dynamicCaches);
    cache.setCaches(combineCacheList);
    cache.initializeCaches();
  }
}
