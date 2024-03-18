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
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.Cache;

import java.util.List;
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
        if (value == null) {
            return;
        }
        if (value instanceof List) {
            if (((List) value).size() == 0) {
                return;
            }
        }
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
