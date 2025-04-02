package musinsa.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;

public class CacheStatsAssertUtils {

  /**
   * 캐시의 통계를 검증 및 로그로 출력
   */
  public static void assertCacheStats(CacheManager cacheManager, String cacheName, Long minHitCount) {

    CaffeineCache springCache = (CaffeineCache) cacheManager.getCache(cacheName);
    if (springCache == null) {
      throw new IllegalArgumentException("캐시 이름이 잘못되었거나 초기화되지 않음 : " + cacheName);
    }

    Cache<Object, Object> nativeCache = springCache.getNativeCache();
    CacheStats stats = nativeCache.stats();

    // 로그 출력
    System.out.println("==== [캐시 통계 : " + cacheName + "] ====");
    System.out.println("캐시 히트 수          : " + stats.hitCount());
    System.out.println("캐시 미스 수         : " + stats.missCount());
    System.out.println("캐시 히트 비율(%)       : " + (stats.hitRate() * 100));
    System.out.println("Load 성공 수 : " + stats.loadSuccessCount());
    System.out.println("Load 실패 수 : " + stats.loadFailureCount());
    System.out.println("총 소요 시간    : " + stats.totalLoadTime() + " ns");
    System.out.println("============================================");

    // 캐시 히트 확인
    assertTrue(stats.hitCount() >= minHitCount, "캐시 히트 수가 기준보다 낮음 : " + stats.hitCount());
  }
}
