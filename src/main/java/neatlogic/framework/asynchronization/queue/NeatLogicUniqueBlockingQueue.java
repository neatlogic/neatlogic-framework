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

package neatlogic.framework.asynchronization.queue;

import com.alibaba.fastjson.JSON;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class NeatLogicUniqueBlockingQueue<T> {
    private static final Logger logger = LoggerFactory.getLogger(NeatLogicUniqueBlockingQueue.class);
    private final BlockingQueue<Task<T>> blockingQueue;
    private final ConcurrentHashMap<String, Boolean> taskMap; // 用于去重

    public NeatLogicUniqueBlockingQueue(int capacity) {
        this.blockingQueue = new LinkedBlockingQueue<>(capacity);
        this.taskMap = new ConcurrentHashMap<>();
    }

    public boolean offer(T t) {
        Task<T> task = new Task<>(t);
        // 保证任务唯一性
        if (taskMap.putIfAbsent(task.getUniqueKey(), Boolean.TRUE) == null) {
            logger.debug("====TagentUpdateInfo-addQueue:" + JSON.toJSONString(task));
            // 如果任务是新任务，放入队列
            boolean added = blockingQueue.offer(task);
            if (!added) {
                // 如果队列已满，移除任务标记
                taskMap.remove(task.getUniqueKey());
                logger.error("Queue is full!");
            }
            return added;
        } else {
            if (t != null) {
                logger.debug("NeatLogicUniqueBlockingQueue repeat： {}", JSON.toJSONString(t));
            }
        }
        return false; // 已存在任务，直接返回 false
    }

    public T take() throws InterruptedException {
        Task<T> task = blockingQueue.take(); // 阻塞式获取任务
        taskMap.remove(task.getUniqueKey()); // 移除已处理任务的唯一标记
        TenantContext tenantContext = TenantContext.get();
        if (tenantContext != null) {
            tenantContext.switchTenant(task.getTenantUuid());
        } else {
            TenantContext.init(task.getTenantUuid());
        }
        return task.getT();
    }

    private static class Task<T> {
        private final T t;
        private final String tenantUuid;

        public Task(T t) {
            this.t = t;
            this.tenantUuid = TenantContext.get().getTenantUuid();
        }

        public T getT() {
            return t;
        }

        public String getTenantUuid() {
            return tenantUuid;
        }

        public String getUniqueKey() {
            // 唯一标识任务的 key，可根据需求定义，例如 `tenantUuid-t.hashCode`
            //System.out.println(tenantUuid + "-" + t.hashCode());
            return tenantUuid + "-" + t.hashCode();
        }
    }

    public int size(){
        return blockingQueue.size();
    }

//    public static void main(String[] args) throws InterruptedException {
//        NeatLogicUniqueBlockingQueue<UserSessionVo> queue = new NeatLogicUniqueBlockingQueue<>(1);
//
//        // 模拟任务插入
//        UserSessionVo a = new UserSessionVo();
//        a.setToken("1111");
//        System.out.println(queue.offer(a)); // 返回 true，任务插入成功
//        UserSessionVo b = new UserSessionVo();
//        b.setToken("222");
//        System.out.println(queue.offer(b)); // 返回 false，任务已存在
//
//        // 模拟任务消费
//        UserSessionVo task = queue.take(); // 消费 "task1"
//        UserSessionVo task2 = queue.take(); // 消费 "task1"
//        UserSessionVo task3 = queue.take(); // 消费 "task1"
//    }
}

