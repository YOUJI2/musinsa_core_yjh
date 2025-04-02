package musinsa.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import musinsa.common.model.CacheType;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CacheConfig {

  @Bean
  public CacheManager cacheManager() {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    // 정적 캐시 생성
    List<CaffeineCache> fixedCaches = Arrays.stream(CacheType.values())
        .map(cache -> new CaffeineCache(cache.getName(), Caffeine.newBuilder()
            .recordStats()
            .initialCapacity(cache.getInitialCapacity())
            .expireAfterWrite(cache.getExpiredTime(), TimeUnit.SECONDS)
            .maximumSize(cache.getMaxSize())
            .build())).toList();

    List<CaffeineCache> caches = new ArrayList<>(fixedCaches);
    cacheManager.setCaches(caches);
    return cacheManager;
  }
}
