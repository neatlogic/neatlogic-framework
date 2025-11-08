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

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * 固定长度的队列,先进先出(线程安全)
 * @param <E>
 */
public class ConcurrentFixedSizeQueue<E> implements Iterable<E> {
    private final ArrayBlockingQueue<E> queue;

    public ConcurrentFixedSizeQueue(int maxEntries) {
        this.queue = new ArrayBlockingQueue<>(maxEntries);
    }

    public boolean offer(E e) {
        if (queue.remainingCapacity() == 0) {
            queue.poll();  // 移除最老的元素以腾出空间
        }
        return queue.offer(e);
    }

    public E poll() {
        return queue.poll();
    }

    public E peek() {
        return queue.peek();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean contains(E e) {
        return queue.contains(e);
    }

    @Override
    public Iterator<E> iterator() {
        return queue.iterator();
    }

    /*public static void main(String[] args) {
        ConcurrentFixedSizeQueue<Integer> queue = new ConcurrentFixedSizeQueue<>(1000);

        // 线程安全地插入和读取数据的测试代码
        for (int i = 0; i < 1100; i++) {
            final int value = i;
            new Thread(() -> {
                queue.offer(value);
                //System.out.println("Added: " + value);
            }).start();
        }

        // 等待所有线程完成
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for(Integer q : queue){
            System.out.println(q);
        }
    }*/
}
