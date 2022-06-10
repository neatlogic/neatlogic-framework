/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.tokenratelimiter;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;

/**
 * 使用软引用做缓存，如果还有空闲内存，就可以暂时保留缓存，当内存不足时清理掉，这样就保证了使用缓存的同时，不会耗尽内存。
 * @param <T>
 */
public class SoftReferenceCache<T> {

    private final Map<Object, SoftEntry<T>> cacheMap;
    private final ReferenceQueue<T> queueOfGarbageCollectedEntries;

    public SoftReferenceCache(Map cacheMap) {
        this.cacheMap = cacheMap;
        this.queueOfGarbageCollectedEntries = new ReferenceQueue<>();
    }

    public void put(Object key, T t) {
        removeGarbageCollectedItems();
        cacheMap.put(key, new SoftEntry(key, t, queueOfGarbageCollectedEntries));
    }

    public T get(Object key) {
        T result = null;
        SoftReference<T> softReference = cacheMap.get(key);
        if (softReference != null) {
            result = softReference.get();
            if (result == null) {
                cacheMap.remove(key);
            }
        }
        return result;
    }

    public Object remove(Object key) {
        removeGarbageCollectedItems();
        SoftEntry<T> value = cacheMap.remove(key);
        if (value != null) {
            return value.get();
        }
        return null;
    }

    public void clear() {
        removeGarbageCollectedItems();
        cacheMap.clear();
    }

    private void removeGarbageCollectedItems() {
        SoftEntry<T> entry;
        while ((entry = (SoftEntry<T>) queueOfGarbageCollectedEntries.poll()) != null) {
            cacheMap.remove(entry.key);
        }
    }

    private static class SoftEntry<T> extends SoftReference<T> {
        private final Object key;

        SoftEntry(Object key, T value, ReferenceQueue<T> garbageCollectionQueue) {
            super(value, garbageCollectionQueue);
            this.key = key;
        }
    }
}
