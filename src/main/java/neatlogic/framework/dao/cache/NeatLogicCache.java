package neatlogic.framework.dao.cache;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * MyBatis 二级缓存实现，基于 Ehcache3
 */
public class NeatLogicCache implements Cache {
    private Integer heapEntries = 1000;
    private Long timeToIdleSeconds;
    private Long timeToLiveSeconds;
    private boolean readOnly = false;

    public void setHeapEntries(Integer heapEntries) {
        if (heapEntries != null && heapEntries > 0) this.heapEntries = heapEntries;
    }

    public void setTimeToIdleSeconds(Long timeToIdleSeconds) {
        this.timeToIdleSeconds = timeToIdleSeconds;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public void setTimeToLiveSeconds(Long timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }

    private static final CacheManager CACHE_MANAGER =
            CacheManagerBuilder.newCacheManagerBuilder().build(true);

    private static final ConcurrentHashMap<String, org.ehcache.Cache<Object, Object>> CACHE_MAP =
            new ConcurrentHashMap<>();

    private final String id;

    public NeatLogicCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    private synchronized org.ehcache.Cache<Object, Object> getCache() {
        String tenant = TenantContext.get().getTenantUuid();
        String cacheName = StringUtils.isNotBlank(tenant) ? tenant + ":" + id : id;
        org.ehcache.Cache<Object, Object> cache = CACHE_MAP.get(cacheName);
        if (cache != null) {
            return cache;
        }
        CacheConfigurationBuilder<Object, Object> builder =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                        Object.class, Object.class,
                        ResourcePoolsBuilder.heap(heapEntries)
                );

        // 过期策略：TTL 优先；否则用 TTI；都没配则不过期
        if (timeToLiveSeconds != null) {
            builder = builder.withExpiry(
                    ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(timeToLiveSeconds))
            );
        } else if (timeToIdleSeconds != null) {
            builder = builder.withExpiry(
                    ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(timeToIdleSeconds))
            );
        }

        try {
            cache = CACHE_MANAGER.createCache(cacheName, builder);
        } catch (Exception e) {
            // 可能已经存在同名 cache，则直接复用
            cache = CACHE_MANAGER.getCache(cacheName, Object.class, Object.class);
            if (cache == null) throw e;
        }
        CACHE_MAP.put(cacheName, cache);
        return cache;
    }

    @Override
    public void clear() {
        getCache().clear();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getObject(Object key) {
        if (!this.readOnly) {
            return CacheUtils.deepCopy(getCache().get(key));
        } else {
            return getCache().get(key);
        }
    }

    @Override
    public int getSize() {
        org.ehcache.Cache<Object, Object> cache = getCache();
        int size = 0;
        for (Object k : cache) {
            size++;
        }
        return size;
    }

    @Override
    public void putObject(Object key, Object value) {
        if (value == null) {
            return;
        }
        if (value instanceof List && ((List<?>) value).isEmpty()) {
            return;
        }
        getCache().put(key, CacheUtils.deepCopy(value));
    }

    @Override
    public Object removeObject(Object key) {
        Object obj = getObject(key);
        getCache().remove(key);
        return obj;
    }

    public void unlock(Object key) {
        // MyBatis 接口要求，但 Ehcache3 不需要
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Cache)) return false;
        Cache otherCache = (Cache) obj;
        return id.equals(otherCache.getId());
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null; // Ehcache3 内部线程安全
    }

    @Override
    public String toString() {
        return "Ehcache3 {" + id + "}";
    }
}
