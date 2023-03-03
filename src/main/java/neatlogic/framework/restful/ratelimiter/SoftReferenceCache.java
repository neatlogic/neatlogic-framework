/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.restful.ratelimiter;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;

/**
 * 使用软引用做缓存，如果还有空闲内存，就可以暂时保留缓存，当内存不足时清理掉，这样就保证了使用缓存的同时，不会耗尽内存。
 * @param <T>
 */
public class SoftReferenceCache<T> {

    //可以通过构造方法传入一个HashMap或者ConcurrentHashMap
    private final Map<Object, SoftEntry<T>> cacheMap;
    //被GC回收的SoftEntry集合
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

    /**
     * 移除已经被GC回收的SoftEntity
     */
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
