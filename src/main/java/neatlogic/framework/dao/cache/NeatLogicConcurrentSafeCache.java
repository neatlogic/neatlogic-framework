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
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 高并发场景下，防止缓存击穿
 */
public class NeatLogicConcurrentSafeCache implements Cache {

    private final static Logger logger = LoggerFactory.getLogger(NeatLogicConcurrentSafeCache.class);
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
        getCache().removeAll();
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
        Object obj = null;
        Element cachedElement = getCache().get(key);
        if (cachedElement != null) {
            obj = cachedElement.getObjectValue();
        }
        if (obj == null) {
            String lockKey = generateLockKey(getId(), key);
            // 1.这里是锁对象放入LOCAL_LOCK_MAP的唯一入口
            // 2.该锁对象会被第一个获取该锁的线程在putObject方法中移除LOCAL_LOCK_MAP
            // 3.其他获取该锁的线程只要释放锁就行
            ReentrantLock lock = LOCAL_LOCK_MAP.computeIfAbsent(lockKey, k -> new ReentrantLock());
            boolean flag = false;
            try {
                flag = lock.tryLock(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // ignore
            }
            cachedElement = getCache().get(key);
            if (cachedElement != null) {
                obj = cachedElement.getObjectValue();
            }
            if (flag) {
                if (obj != null) {
                    if (LOCAL_LOCK_MAP.get(lockKey) == lock) {
                        logger.warn("NeatLogicConcurrentSafeCache.LOCAL_LOCK_MAP中的锁对象没有被正常移除，lockKey = " + lockKey);
                    }
                    // 获取到锁后，从缓存中得到的结果不为null，不会再查询数据库，也不会调用putObject方法，所以要在这里释放该锁
                    lock.unlock();
                } else {
                    // 获取到锁后，从缓存中得到的结果为null，有以下3种情况：
                    // 1.该线程是第一个获取到该锁的线程，需要去查询数据库，该线程会在putObject方法中释放该锁。
                    // 2.有其他线程调用了clear方法，清空了缓存。
                    // 3.该SQL语句执行报错。
                    // 对应第2和3种情况，第一个获取到该锁的线程，在调用putObject()方法时会在LOCAL_LOCK_MAP删除锁，会出现一种场景，这里获得锁，但LOCAL_LOCK_MAP中已经删除了该锁，必须在这里释放锁
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
        return obj;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSize() {
        return getCache().getSize();
    }

    /**
     * 不管SQL语句执行是否抛异常，都会调用putObject方法
     * SQL语句执行成功得到结果为null时，value为[]
     * SQL语句执行异常时，value为null
     * {@inheritDoc}
     */
    @Override
    public void putObject(Object key, Object value) {
        getCache().put(new Element(key, value));
        String lockKey = generateLockKey(getId(), key);
        ReentrantLock lock = LOCAL_LOCK_MAP.get(lockKey);
        if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
            LOCAL_LOCK_MAP.remove(lockKey, lock);
            lock.unlock();
        }
    }

    /**
     * 对应二级缓存，该方法不会被调用
     * {@inheritDoc}
     */
    @Override
    public Object removeObject(Object key) {
        Object obj = null;
        Ehcache ehcache = getCache();
        Element cachedElement = ehcache.get(key);
        if (cachedElement != null) {
            obj = cachedElement.getObjectValue();
            ehcache.remove(key);
        }
        String lockKey = generateLockKey(getId(), key);
        ReentrantLock lock = LOCAL_LOCK_MAP.get(lockKey);
        if (lock != null && lock.isLocked() && lock.isHeldByCurrentThread()) {
            LOCAL_LOCK_MAP.remove(lockKey, lock);
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

    public static List<String> getAllLockKeyList() {
        List<String> resultList = new ArrayList<>();
        for (Map.Entry<String, ReentrantLock> entry : LOCAL_LOCK_MAP.entrySet()) {
            resultList.add(entry.getKey());
        }
        return resultList;
    }
}
