package codedriver.framework.dao.cache;

import java.util.concurrent.locks.ReadWriteLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.cache.Cache;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

public class CodeDriverCache implements Cache {
	/**
	 * The cache manager reference.
	 */
	protected static CacheManager CACHE_MANAGER = CacheManager.create();

	/**
	 * The cache id (namespace)
	 */
	protected final String id;

	/**
	 * The cache instance
	 */
	protected Ehcache cache;

	/**
	 * @param id
	 */
	public CodeDriverCache(final String id) {
		if (id == null) {
			throw new IllegalArgumentException("Cache instances require an ID");
		}
		this.id = id;
		TenantContext tenantContext = TenantContext.get();
		String tenant = null;
		if (tenantContext != null) {
			tenant = tenantContext.getTenantUuid();
		}
		if (StringUtils.isNotBlank(tenant)) {
			if (!CACHE_MANAGER.cacheExists(tenant + ":" + id)) {
				CACHE_MANAGER.addCache(tenant + ":" + id);
			}
			this.cache = CACHE_MANAGER.getEhcache(tenant + ":" + id);
		} else {
			if (!CACHE_MANAGER.cacheExists(id)) {
				CACHE_MANAGER.addCache(id);
			}
			this.cache = CACHE_MANAGER.getEhcache(id);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		cache.removeAll();
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
		Element cachedElement = cache.get(key);
		if (cachedElement == null) {
			return null;
		}
		return cachedElement.getObjectValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		return cache.getSize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putObject(Object key, Object value) {
		cache.put(new Element(key, value));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object removeObject(Object key) {
		Object obj = getObject(key);
		cache.remove(key);
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
	 * @param timeToIdleSeconds
	 *            the default amount of time to live for an element from its
	 *            last accessed or modified date
	 */
	public void setTimeToIdleSeconds(long timeToIdleSeconds) {
		cache.getCacheConfiguration().setTimeToIdleSeconds(timeToIdleSeconds);
	}

	/**
	 * Sets the time to idle for an element before it expires. Is only used if
	 * the element is not eternal.
	 *
	 * @param timeToLiveSeconds
	 *            the default amount of time to live for an element from its
	 *            creation date
	 */
	public void setTimeToLiveSeconds(long timeToLiveSeconds) {
		cache.getCacheConfiguration().setTimeToLiveSeconds(timeToLiveSeconds);
	}

	/**
	 * Sets the maximum objects to be held in memory (0 = no limit).
	 *
	 * @param maxElementsInMemory
	 *            The maximum number of elements in memory, before they are
	 *            evicted (0 == no limit)
	 */
	public void setMaxEntriesLocalHeap(long maxEntriesLocalHeap) {
		cache.getCacheConfiguration().setMaxEntriesLocalHeap(maxEntriesLocalHeap);
	}

	/**
	 * Sets the maximum number elements on Disk. 0 means unlimited.
	 *
	 * @param maxElementsOnDisk
	 *            the maximum number of Elements to allow on the disk. 0 means
	 *            unlimited.
	 */
	public void setMaxEntriesLocalDisk(long maxEntriesLocalDisk) {
		cache.getCacheConfiguration().setMaxEntriesLocalDisk(maxEntriesLocalDisk);
	}

	/**
	 * Sets the eviction policy. An invalid argument will set it to null.
	 *
	 * @param memoryStoreEvictionPolicy
	 *            a String representation of the policy. One of "LRU", "LFU" or
	 *            "FIFO".
	 */
	public void setMemoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
		cache.getCacheConfiguration().setMemoryStoreEvictionPolicy(memoryStoreEvictionPolicy);
	}

}
