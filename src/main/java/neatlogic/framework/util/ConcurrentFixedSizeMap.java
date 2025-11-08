/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 固定长度的map,先进先出(线程安全)
 * @param <K>
 * @param <V>
 */
public class ConcurrentFixedSizeMap<K, V> {
    private final ConcurrentHashMap<K, V> map;
    private final ConcurrentLinkedQueue<K> queue;
    private final int maxEntries;
    private final Lock lock = new ReentrantLock();

    public ConcurrentFixedSizeMap(int maxEntries) {
        this.map = new ConcurrentHashMap<>(maxEntries);
        this.queue = new ConcurrentLinkedQueue<>();
        this.maxEntries = maxEntries;
    }

    public void put(K key, V value) {
        lock.lock();
        try {
            if (map.size() >= maxEntries) {
                K oldestKey = queue.poll();
                if (oldestKey != null) {
                    map.remove(oldestKey);
                }
            }
            if (map.put(key, value) == null) {
                queue.add(key);
            }
        } finally {
            lock.unlock();
        }
    }

    public V get(K key) {
        return map.get(key);
    }

    public int size() {
        return map.size();
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public V remove(K key) {
        lock.lock();
        try {
            queue.remove(key);
            return map.remove(key);
        } finally {
            lock.unlock();
        }
    }

    /*public static void main(String[] args) {
        ConcurrentFixedSizeMap<Integer, String> map = new ConcurrentFixedSizeMap<>(1000);

        // 线程安全地插入和读取数据的测试代码
        for (int i = 0; i < 1100; i++) {
            final int key = i;
            new Thread(() -> map.put(key, "Value " + key)).start();
        }

        // 等待所有线程完成
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Map size: " + map.size());
        for (Map.Entry entry : map.map.entrySet()){
            System.out.println(entry.getValue());
        }
    }*/
}

