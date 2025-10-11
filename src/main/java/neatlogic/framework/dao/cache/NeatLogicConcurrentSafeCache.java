package neatlogic.framework.dao.cache;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 高并发场景下，防止缓存击穿 (Ehcache3 版本)
 */
public class NeatLogicConcurrentSafeCache implements Cache {

    private static final Logger logger = LoggerFactory.getLogger(NeatLogicConcurrentSafeCache.class);
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

    private static final ConcurrentHashMap<String, org.ehcache.Cache<Object, Object>> CACHE_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ReentrantLock> LOCAL_LOCK_MAP = new ConcurrentHashMap<>();

    private static String generateLockKey(String id, Object key) {
        String tenant = TenantContext.get().getTenantUuid();
        if (StringUtils.isNotBlank(tenant)) {
            return tenant + ":" + id + ":" + key;
        } else {
            return id + ":" + key;
        }
    }

    protected final String id;

    public NeatLogicConcurrentSafeCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    /*
    private synchronized org.ehcache.Cache<Object, Object> getCache() {
        String tenant = TenantContext.get().getTenantUuid();
        String cacheName = StringUtils.isNotBlank(tenant) ? tenant + ":" + id : id;

        return CACHE_MAP.computeIfAbsent(cacheName, name -> {
            CacheConfigurationBuilder<Object, Object> config =
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(
                            Object.class, Object.class,
                            ResourcePoolsBuilder.heap(1000)
                    ).withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(300)));
            return CACHE_MANAGER.createCache(name, config);
        });
    }*/

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
        Object obj = getCache().get(key);
        if (obj == null) {
            String lockKey = generateLockKey(getId(), key);
            ReentrantLock lock = LOCAL_LOCK_MAP.computeIfAbsent(lockKey, k -> new ReentrantLock());
            boolean flag = false;
            try {
                flag = lock.tryLock(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
            obj = getCache().get(key);
            if (flag) {
                if (obj != null) {
                    if (LOCAL_LOCK_MAP.get(lockKey) == lock) {
                        logger.warn("NeatLogicConcurrentSafeCache.LOCAL_LOCK_MAP中的锁对象没有被正常移除，lockKey = " + lockKey);
                    }
                    lock.unlock();
                } else {
                    ReentrantLock reentrantLock = LOCAL_LOCK_MAP.get(lockKey);
                    if (reentrantLock != lock) {
                        lock.unlock();
                    }
                }
            } else {
                RuntimeException ex = new RuntimeException("NeatLogicConcurrentSafeCache 获取锁超时 lockKey = " + lockKey);
                logger.warn(ex.getMessage(), ex);
            }
        }
        if (!this.readOnly) {
            return CacheUtils.deepCopy(obj);
        } else {
            return obj;
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
        getCache().put(key, value);
        String lockKey = generateLockKey(getId(), key);
        ReentrantLock lock = LOCAL_LOCK_MAP.get(lockKey);
        if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
            LOCAL_LOCK_MAP.remove(lockKey, lock);
            lock.unlock();
        }
    }

    @Override
    public Object removeObject(Object key) {
        Object obj = getCache().get(key);
        getCache().remove(key);

        String lockKey = generateLockKey(getId(), key);
        ReentrantLock lock = LOCAL_LOCK_MAP.get(lockKey);
        if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
            LOCAL_LOCK_MAP.remove(lockKey, lock);
            lock.unlock();
        }
        return obj;
    }

    public void unlock(Object key) {
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
        return null;
    }

    @Override
    public String toString() {
        return "Ehcache3 {" + id + "}";
    }

    public static List<String> getAllLockKeyList() {
        List<String> resultList = new ArrayList<>();
        for (Map.Entry<String, ReentrantLock> entry : LOCAL_LOCK_MAP.entrySet()) {
            resultList.add(entry.getKey());
        }
        return resultList;
    }
}