package neatlogic.framework.dao.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

public class UserSessionCache {
    private static CacheManager CACHE_MANAGER;

    private synchronized static Ehcache getCache() {
        if (CACHE_MANAGER == null) {
            CacheConfiguration cacheConfiguration = new CacheConfiguration();
            cacheConfiguration.setName("UserSessionCache");
            cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
            cacheConfiguration.setMaxEntriesLocalHeap(1000);
            cacheConfiguration.internalSetTimeToIdle(300);
            cacheConfiguration.internalSetTimeToLive(600);
            Configuration config = new Configuration();
            config.addCache(cacheConfiguration);
            CACHE_MANAGER = CacheManager.newInstance(config);
        }
        if (!CACHE_MANAGER.cacheExists("UserSessionCache")) {
            CACHE_MANAGER.addCache("UserSessionCache");
        }
        return CACHE_MANAGER.getEhcache("UserSessionCache");
    }

    public static void addItem(String key, Object item) {
        getCache().put(new Element(key, item));
    }

    public static Object getItem(String key) {
        Element cachedElement = getCache().get(key);
        if (cachedElement == null) {
            return null;
        }
        return cachedElement.getObjectValue();
    }

    public static void removeItem(String key) {
        getCache().remove(key);
    }
}
