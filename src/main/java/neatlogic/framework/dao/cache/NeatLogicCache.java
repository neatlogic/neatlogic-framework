/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.dao.cache;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.Cache;

import java.util.concurrent.locks.ReadWriteLock;

public class NeatLogicCache implements Cache {
    /**
     * The cache manager reference.
     */
    protected static CacheManager CACHE_MANAGER = CacheManager.create();

    /**
     * The cache id (namespace)
     */
    protected final String id;


    public NeatLogicCache(final String id) {
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
                CACHE_MANAGER.addCache(tenant + ":" + id);
            }
            return CACHE_MANAGER.getEhcache(tenant + ":" + id);
        } else {
            if (!CACHE_MANAGER.cacheExists(id)) {
                CACHE_MANAGER.addCache(id);
            }
            return CACHE_MANAGER.getEhcache(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        //System.out.println(System.currentTimeMillis() + ":clear cache:" + this.id);
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
        Element cachedElement = getCache().get(key);
        if (cachedElement == null) {
            return null;
        }
        /*if (key.toString().contains("getAttrByCiId")) {
            System.out.println(System.currentTimeMillis() + ":match getAttrByCiId cached,value=" + cachedElement.getObjectValue());
        }*/
        return cachedElement.getObjectValue();
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object removeObject(Object key) {
        Object obj = getObject(key);
        getCache().remove(key);
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
