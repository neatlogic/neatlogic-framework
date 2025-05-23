/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.dao.cache;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.Cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 高并发场景下，防止缓存击穿
 */
public class NeatLogicConcurrentSafeCache implements Cache {
    /**
     * The cache manager reference.
     */
    protected static CacheManager CACHE_MANAGER = CacheManager.create();
    private static final ConcurrentHashMap<String, ReentrantLock> LOCAL_LOCK_MAP = new ConcurrentHashMap<>();

    private static String generateLockKey(String id, Object key) {
        String tenant = null;
        TenantContext tenantContext = TenantContext.get();
        if (tenantContext != null) {
            tenant = tenantContext.getTenantUuid();
        }
        if (StringUtils.isNotBlank(tenant)) {
            return tenant + ":" + id + ":" + key;
        } else {
            return id + ":" + key;
        }
    }
    /**
     * The cache id (namespace)
     */
    protected final String id;


    public NeatLogicConcurrentSafeCache(final String id) {
        if (id == null) {
            throw new IllegalArgumentException("Cache instances require an ID");
        }
        this.id = id;
    }

    private synchronized Ehcache getCache() {
        TenantContext tenantContext = TenantContext.get();
        String tenant = null;
        if (tenantContext != null) {
            tenant = tenantContext.getTenantUuid();
        }
        if (StringUtils.isNotBlank(tenant)) {
            if (!CACHE_MANAGER.cacheExists(tenant + ":" + id)) {
                Ehcache ehcache = CACHE_MANAGER.addCacheIfAbsent(tenant + ":" + id);
                CacheConfiguration cacheConfiguration = ehcache.getCacheConfiguration();
                // 缓存5分钟
                cacheConfiguration.setTimeToIdleSeconds(300);
                cacheConfiguration.setTimeToLiveSeconds(300);
            }
            return CACHE_MANAGER.getEhcache(tenant + ":" + id);
        } else {
            if (!CACHE_MANAGER.cacheExists(id)) {
                Ehcache ehcache = CACHE_MANAGER.addCacheIfAbsent(id);
                CacheConfiguration cacheConfiguration = ehcache.getCacheConfiguration();
                // 缓存5分钟
                cacheConfiguration.setTimeToIdleSeconds(300);
                cacheConfiguration.setTimeToLiveSeconds(300);
            }
            return CACHE_MANAGER.getEhcache(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        Ehcache cache = getCache();
        List keys = cache.getKeys();
        cache.removeAll();
        if (CollectionUtils.isNotEmpty(keys)) {
            for (Object key : keys) {
                ReentrantLock lock = LOCAL_LOCK_MAP.remove(generateLockKey(getId(), key));
                if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getObject(Object key) {
        Element cachedElement = getCache().get(key);
        if (cachedElement != null) {
            return cachedElement.getObjectValue();
        }
        ReentrantLock lock = LOCAL_LOCK_MAP.computeIfAbsent(generateLockKey(getId(), key), k -> new ReentrantLock());
        try {
            boolean flag = lock.tryLock(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }
        cachedElement = getCache().get(key);
        if (cachedElement != null) {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            return cachedElement.getObjectValue();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return getCache().getSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putObject(Object key, Object value) {
        getCache().put(new Element(key, value));
        ReentrantLock lock = LOCAL_LOCK_MAP.remove(generateLockKey(getId(), key));
        if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object removeObject(Object key) {
        Object obj = getObject(key);
        getCache().remove(key);
        ReentrantLock lock = LOCAL_LOCK_MAP.remove(generateLockKey(getId(), key));
        if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
        return obj;
    }

    /**
     * {@inheritDoc}
     */
    public void unlock(Object key) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Cache)) {
            return false;
        }

        Cache otherCache = (Cache) obj;
        return id.equals(otherCache.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "EHCache {" + id + "}";
    }

    // DYNAMIC PROPERTIES

    /**
     * Sets the time to idle for an element before it expires. Is only used if
     * the element is not eternal.
     *
     * @param timeToIdleSeconds the default amount of time to live for an element from its
     *                          last accessed or modified date
     */
    public void setTimeToIdleSeconds(long timeToIdleSeconds) {
        getCache().getCacheConfiguration().setTimeToIdleSeconds(timeToIdleSeconds);
    }

    /**
     * Sets the time to idle for an element before it expires. Is only used if
     * the element is not eternal.
     *
     * @param timeToLiveSeconds the default amount of time to live for an element from its
     *                          creation date
     */
    public void setTimeToLiveSeconds(long timeToLiveSeconds) {
        getCache().getCacheConfiguration().setTimeToLiveSeconds(timeToLiveSeconds);
    }

    /**
     * Sets the maximum objects to be held in memory (0 = no limit).
     * evicted (0 == no limit)
     */
    public void setMaxEntriesLocalHeap(long maxEntriesLocalHeap) {
        getCache().getCacheConfiguration().setMaxEntriesLocalHeap(maxEntriesLocalHeap);
    }

    /**
     * Sets the maximum number elements on Disk. 0 means unlimited.
     * unlimited.
     */
    public void setMaxEntriesLocalDisk(long maxEntriesLocalDisk) {
        getCache().getCacheConfiguration().setMaxEntriesLocalDisk(maxEntriesLocalDisk);
    }

    /**
     * Sets the eviction policy. An invalid argument will set it to null.
     *
     * @param memoryStoreEvictionPolicy a String representation of the policy. One of "LRU", "LFU" or
     *                                  "FIFO".
     */
    public void setMemoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
        getCache().getCacheConfiguration().setMemoryStoreEvictionPolicy(memoryStoreEvictionPolicy);
    }

}
