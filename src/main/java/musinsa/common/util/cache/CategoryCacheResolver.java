package musinsa.common.util.cache;

import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import musinsa.common.exception.defined.BusinessException;
import musinsa.common.model.ErrorCode;
import musinsa.domain.category.entity.Category;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component("categoryCacheResolver")
@RequiredArgsConstructor
public class CategoryCacheResolver implements CacheResolver {

  private final CacheManager cacheManager;

  @Override
  public @NonNull Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
    Object arg = context.getArgs()[0];

    if (!(arg instanceof Category category)) {
      throw new BusinessException(ErrorCode.INVALID_CATEGORY_TYPE);
    }

    String categoryName = category.getName().toLowerCase();
    String dynamicCacheName = "category_type_" + categoryName;

    Cache cache = cacheManager.getCache(dynamicCacheName);
    if (cache == null) {
      throw new BusinessException(ErrorCode.CACHE_NOT_FOUND_ERROR);
    }

    return List.of(cache);
  }
}
