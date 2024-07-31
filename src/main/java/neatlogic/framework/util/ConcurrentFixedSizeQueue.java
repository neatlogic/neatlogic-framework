/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
