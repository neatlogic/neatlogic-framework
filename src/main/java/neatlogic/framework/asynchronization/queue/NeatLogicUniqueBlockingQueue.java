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
    private final BlockingQueue<QueueTask<T>> blockingQueue;
    private final ConcurrentHashMap<String, Boolean> taskMap; // 用于去重

    public NeatLogicUniqueBlockingQueue(int capacity) {
        this.blockingQueue = new LinkedBlockingQueue<>(capacity);
        this.taskMap = new ConcurrentHashMap<>();
    }

    public NeatLogicUniqueBlockingQueue() {
        this.blockingQueue = new LinkedBlockingQueue<>();
        this.taskMap = new ConcurrentHashMap<>();
    }

    public boolean offer(T t) {
        QueueTask<T> task = new QueueTask<>(t);
        // 保证任务唯一性
        if (taskMap.putIfAbsent(task.getUniqueKey(), Boolean.TRUE) == null) {
            logger.debug("====TagentUpdateInfo-addQueue:{}", JSON.toJSONString(task));
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
        QueueTask<T> task = blockingQueue.take(); // 阻塞式获取任务
        taskMap.remove(task.getUniqueKey()); // 移除已处理任务的唯一标记
        TenantContext tenantContext = TenantContext.get();
        if (tenantContext != null) {
            tenantContext.switchTenant(task.getTenantUuid());
        } else {
            TenantContext.init(task.getTenantUuid());
        }
        return task.getT();
    }


    public int size() {
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

