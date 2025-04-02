package musinsa.common.util.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musinsa.common.model.CacheType;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheEvictionManager {

  private final CacheManager cacheManager;

  public void evictAllCaches() {
    // 모든 캐시 초기화
    List<String> cacheNames = new ArrayList<>(cacheManager.getCacheNames());
    evictCaches(cacheNames);
  }

  public void evictCachesByCondition(boolean isLowest, boolean isHighest, String categoryName) {
    // 최저가인 경우 (모든 API에 관련된 캐시 초기화)
    // 최대값인 경우 (특정 카테고리 상품 가격 캐시만 초기화)
    Set<String> cacheKeys = new HashSet<>();

    if (isLowest) {
      cacheKeys.add(CacheType.CATEGORY_LOWEST_PRICE_RANK.getName());
      cacheKeys.add(CacheType.BRAND_RANK.getName());
    }

    if (isLowest || isHighest) {
      cacheKeys.add("category_type_" + categoryName);
    }

    evictCaches(List.copyOf(cacheKeys));
  }

  private void evictCaches(List<String> cacheKeys) {
    for (String cacheKey : cacheKeys) {
      Cache cache = cacheManager.getCache(cacheKey);
      if (cache != null) {
        cache.clear();
      } else {
        log.warn("해당 캐시가 존재하지 않습니다 [ CacheKey ] : {}", cacheKey);
      }
    }
  }
}
