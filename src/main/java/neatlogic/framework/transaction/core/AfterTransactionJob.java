/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.transaction.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AfterTransactionJob<T> {
    public enum EVENT {
        COMMITTED(), COMPLETED()
    }

    private static final Logger logger = LoggerFactory.getLogger(AfterTransactionJob.class);

    private final ThreadLocal<Set<T>> THREADLOCAL = new ThreadLocal<>();
    private final ThreadLocal<Set<NeatLogicThread>> T_THREADLOCAL = new ThreadLocal<>();
    private final ThreadLocal<List<NeatLogicThread>> LIST_THREADLOCAL = new ThreadLocal<>();
    private final String threadName;

    public AfterTransactionJob(String _threadName) {
        threadName = _threadName;
    }


    /**
     * 异步执行
     *
     * @param t         对象
     * @param committed 提交时执行主体
     */
    public void execute(T t, ICommitted<T> committed) {
        execute(t, committed, null, false);
    }


    /**
     * 异步执行
     *
     * @param t         对象
     * @param committed 提交时执行主体
     * @param completed 完成时执行主体
     */
    public void execute(T t, ICommitted<T> committed, ICompleted<T> completed) {
        execute(t, committed, completed, false);
    }

    /**
     * 控制异步还是同步执行
     *
     * @param t        对象
     * @param commited 提交时执行
     * @param isSync   是否同步执行
     */
    public void execute(T t, ICommitted<T> commited, boolean isSync) {
        execute(t, commited, null, isSync);
    }

    /**
     * 事务提交后执行线程
     *
     * @param t 线程
     */
    public void execute(NeatLogicThread t) {
        execute(t, EVENT.COMMITTED);
    }

    /**
     * 事务提交或完成后执行线程
     *
     * @param t     线程
     * @param event 事务时间，提交或完成
     */
    public void execute(NeatLogicThread t, EVENT event) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            CachedThreadPool.execute(t);
        } else {
            Set<NeatLogicThread> tList = T_THREADLOCAL.get();
            if (tList == null) {
                tList = new HashSet<>();
                T_THREADLOCAL.set(tList);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        if (event == EVENT.COMMITTED) {
                            Set<NeatLogicThread> tList = T_THREADLOCAL.get();
                            for (NeatLogicThread t : tList) {
                                CachedThreadPool.execute(t);
                            }
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (event == EVENT.COMPLETED) {
                            Set<NeatLogicThread> tList = T_THREADLOCAL.get();
                            for (NeatLogicThread t : tList) {
                                CachedThreadPool.execute(t);
                            }
                        }
                    }
                });
            }
            tList.add(t);
        }
    }

    /**
     * 事务提交后按顺序执行线程任务
     *
     * @param t 线程任务
     */
    public void executeInOrder(NeatLogicThread t) {
        executeInOrder(t, EVENT.COMMITTED);
    }

    /**
     * 事务提交或完成后按顺序执行线程任务
     *
     * @param t 线程任务
     * @param event 事务时间，提交或完成
     */
    public void executeInOrder(NeatLogicThread t, EVENT event) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            CachedThreadPool.execute(t);
        } else {
            List<NeatLogicThread> tList = LIST_THREADLOCAL.get();
            if (tList == null) {
                tList = new ArrayList<>();
                LIST_THREADLOCAL.set(tList);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        if (event == EVENT.COMMITTED) {
                            List<NeatLogicThread> tList = LIST_THREADLOCAL.get();
                            for (int i = 0; i < tList.size(); i++) {
                                NeatLogicThread t = tList.get(i);
                                CountDownLatch latch = new CountDownLatch(1);
                                CachedThreadPool.execute(t, latch);
                                if (i < tList.size() - 1) {
                                    try {
                                        boolean flag = latch.await(10, TimeUnit.SECONDS);
                                    } catch (InterruptedException e) {
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (event == EVENT.COMPLETED) {
                            List<NeatLogicThread> tList = LIST_THREADLOCAL.get();
                            for (int i = 0; i < tList.size(); i++) {
                                NeatLogicThread t = tList.get(i);
                                CountDownLatch latch = new CountDownLatch(1);
                                CachedThreadPool.execute(t, latch);
                                if (i < tList.size() - 1) {
                                    try {
                                        boolean flag = latch.await(1, TimeUnit.MINUTES);
                                    } catch (InterruptedException e) {
                                        logger.error(e.getMessage(), e);
                                    }
                                }
                            }
                        }
                    }
                });
            }
            tList.add(t);
        }
    }

    /*
     * @Description:
     * @Author: chenqiwei
     * @Date: 2021/1/7 4:06 下午
     * @Params: [参数, 事务提交后回调函数, 事务结束后回调函数（回滚也会触发）]
     * @Returns: void
     **/
    public synchronized void execute(T t, ICommitted<T> commited, ICompleted<T> completed, boolean isSync) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            if (commited != null) {
                if (!isSync) {
                    CachedThreadPool.execute(new NeatLogicThread(this.threadName + "-COMMITED") {
                        @Override
                        protected void execute() {
                            commited.execute(t);
                        }
                    });
                } else {
                    commited.execute(t);
                }
            }
            if (completed != null) {
                if (!isSync) {
                    CachedThreadPool.execute(new NeatLogicThread(this.threadName + "COMPLETED") {
                        @Override
                        protected void execute() {
                            completed.execute(t);
                        }
                    });
                } else {
                    completed.execute(t);
                }
            }
        } else {
            Set<T> tList = THREADLOCAL.get();
            if (tList == null) {
                tList = new HashSet<>();
                THREADLOCAL.set(tList);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        if (commited != null) {
                            Set<T> tList = THREADLOCAL.get();
                            if (!isSync) {
                                CachedThreadPool.execute(new NeatLogicThread(threadName + "-COMMITTED") {
                                    @Override
                                    protected void execute() {
                                        for (T t : tList) {
                                            commited.execute(t);
                                        }
                                    }
                                });
                            } else {
                                for (T t : tList) {
                                    commited.execute(t);
                                }
                            }
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (completed != null) {
                            Set<T> tList = THREADLOCAL.get();
                            if (commited != null) {
                                CachedThreadPool.execute(new NeatLogicThread(threadName + "COMPLETED") {
                                    @Override
                                    protected void execute() {
                                        for (T t : tList) {
                                            completed.execute(t);
                                        }
                                    }
                                });
                            } else {
                                for (T t : tList) {
                                    completed.execute(t);
                                }
                            }
                        }
                        THREADLOCAL.remove();
                    }
                });
            }
            tList.add(t);
        }
    }
}
