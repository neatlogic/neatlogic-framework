/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.store.mysql;

import com.zaxxer.hikari.metrics.IMetricsTracker;
import com.zaxxer.hikari.metrics.MetricsTrackerFactory;
import com.zaxxer.hikari.metrics.PoolStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionTimeMetricsFactory implements MetricsTrackerFactory {
    private static final Logger logger = LoggerFactory.getLogger("dataSourceConnectionTimeMetrics");
    /**
     * Create an instance of an IMetricsTracker.
     *
     * @param poolName  the name of the pool
     * @param poolStats a PoolStats instance to use
     * @return a IMetricsTracker implementation instance
     */
    @Override
    public IMetricsTracker create(String poolName, PoolStats poolStats) {

        IMetricsTracker metricsTracker = new IMetricsTracker() {
            /**
             * 触发时机: 尝试创建新的物理数据库连接时
             * 作用: 记录创建连接耗时
             * @param connectionCreatedMillis
             */
            @Override
            public void recordConnectionCreatedMillis(long connectionCreatedMillis) {
                if (logger.isDebugEnabled()) {
                    logger.debug("HikariCP [{}] 创建数据库连接耗时: {} ms，活跃连接数: {}，空闲连接数: {}，总连接数: {}，等待连接线程数: {}，最小空闲连接数: {}，允许最大连接数: {}",
                            poolName, connectionCreatedMillis,
                            poolStats.getActiveConnections(),
                            poolStats.getIdleConnections(),
                            poolStats.getTotalConnections(),
                            poolStats.getPendingThreads(),
                            poolStats.getMinConnections(),
                            poolStats.getMaxConnections()
                    );
                }
                if (logger.isWarnEnabled() && connectionCreatedMillis > 1000) {
                    logger.warn("HikariCP [{}] 数据库连接创建缓慢: {} ms，活跃连接数: {}，空闲连接数: {}，总连接数: {}，等待连接线程数: {}，最小空闲连接数: {}，允许最大连接数: {}",
                            poolName, connectionCreatedMillis,
                            poolStats.getActiveConnections(),
                            poolStats.getIdleConnections(),
                            poolStats.getTotalConnections(),
                            poolStats.getPendingThreads(),
                            poolStats.getMinConnections(),
                            poolStats.getMaxConnections()
                    );
                }
            }

            /**
             * 触发时机: 从连接池成功获取连接，或获取连接超时时
             * 作用: 记录获取连接耗时
             * @param elapsedAcquiredNanos
             */
            @Override
            public void recordConnectionAcquiredNanos(long elapsedAcquiredNanos) {
                if (logger.isDebugEnabled()) {
                    double millis = elapsedAcquiredNanos / 1_000_000.0;
                    if (millis > 500) {
                        logger.debug("HikariCP [{}] 获取连接耗时: {} ms，活跃连接数: {}，空闲连接数: {}，总连接数: {}，等待连接线程数: {}，最小空闲连接数: {}，允许最大连接数: {}",
                                poolName, millis,
                                poolStats.getActiveConnections(),
                                poolStats.getIdleConnections(),
                                poolStats.getTotalConnections(),
                                poolStats.getPendingThreads(),
                                poolStats.getMinConnections(),
                                poolStats.getMaxConnections()
                        );
                    }
                }
            }

            /**
             * 触发时机: 业务归还连接到连接池时
             * 作用: 记录连接被业务占用的时间
             * @param elapsedBorrowedMillis
             */
            @Override
            public void recordConnectionUsageMillis(long elapsedBorrowedMillis) {
                if (logger.isWarnEnabled()) {
                    if (elapsedBorrowedMillis > 5000) {
                        logger.warn("HikariCP [{}] 连接被业务代码占用的时间: {} ms", poolName, elapsedBorrowedMillis);
                    }
                }
            }

            /**
             * 触发时机: 从连接池获取连接超时
             * 作用: 记录一次获取连接超时事件
             */
            @Override
            public void recordConnectionTimeout() {
                if (logger.isWarnEnabled()) {
                    logger.warn("HikariCP [{}] 从连接池获取连接超时，活跃连接数: {}，空闲连接数: {}，总连接数: {}，等待连接线程数: {}，最小空闲连接数: {}，允许最大连接数: {}",
                            poolName,
                            poolStats.getActiveConnections(),
                            poolStats.getIdleConnections(),
                            poolStats.getTotalConnections(),
                            poolStats.getPendingThreads(),
                            poolStats.getMinConnections(),
                            poolStats.getMaxConnections()
                    );
                }
            }

            /**
             * 触发时机: 连接池关闭时
             * 作用: 释放监控器自身的资源
             */
            @Override
            public void close() {
                if (logger.isWarnEnabled()) {
                    logger.warn("HikariCP [{}] 连接池关闭", poolName);
                }
            }
        };
        return metricsTracker;
    }
}
