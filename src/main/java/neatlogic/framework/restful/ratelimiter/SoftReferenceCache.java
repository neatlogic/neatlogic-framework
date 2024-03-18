/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
