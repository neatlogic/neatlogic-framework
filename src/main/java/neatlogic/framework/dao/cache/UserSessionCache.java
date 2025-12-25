package neatlogic.framework.dao.cache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.time.Duration;

public class UserSessionCache {

    private static CacheManager CACHE_MANAGER;
    private static Cache<String, Object> CACHE;

    private static synchronized Cache<String, Object> getCache() {
        if (CACHE_MANAGER == null) {
            CACHE_MANAGER = CacheManagerBuilder.newCacheManagerBuilder().build(true);

            CacheConfigurationBuilder<String, Object> cacheConfig =
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(
                            String.class,
                            Object.class,
                            ResourcePoolsBuilder.heap(2000) // 最大堆内存条目数
                    ).withExpiry(
                            ExpiryPolicyBuilder.expiry()
                                    .create(Duration.ofSeconds(900))  // TTL: 创建后 900s 过期
                                    //.access(Duration.ofSeconds(900))  // TTI: 最后访问后 900s 过期
                                    //.update(Duration.ofSeconds(900))  // 更新后 900s 过期（按需）
                                    .build()
                    );

            CACHE = CACHE_MANAGER.createCache("UserSessionCache", cacheConfig);
        }
        return CACHE;
    }

    public static void addItem(String key, Object item) {
        getCache().put(key, item);
    }

    public static Object getItem(String key) {
        return getCache().get(key);
    }

    public static void removeItem(String key) {
        getCache().remove(key);
    }

    public static boolean containsKey(String key) {
        return getCache().containsKey(key);
    }
}