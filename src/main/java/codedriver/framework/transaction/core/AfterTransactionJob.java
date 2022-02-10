/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.transaction.core;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashSet;
import java.util.Set;

public class AfterTransactionJob<T> {
    public enum EVENT {
        COMMITTED(), COMPLETED()
    }


    private final ThreadLocal<Set<T>> THREADLOCAL = new ThreadLocal<>();
    private final ThreadLocal<Set<CodeDriverThread>> T_THREADLOCAL = new ThreadLocal<>();
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
    public void execute(CodeDriverThread t) {
        execute(t, EVENT.COMMITTED);
    }

    /**
     * 事务提交或完成后执行线程
     *
     * @param t     线程
     * @param event 事务时间，提交或完成
     */
    public void execute(CodeDriverThread t, EVENT event) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            CachedThreadPool.execute(t);
        } else {
            Set<CodeDriverThread> tList = T_THREADLOCAL.get();
            if (tList == null) {
                tList = new HashSet<>();
                T_THREADLOCAL.set(tList);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        if (event == EVENT.COMMITTED) {
                            Set<CodeDriverThread> tList = T_THREADLOCAL.get();
                            for (CodeDriverThread t : tList) {
                                CachedThreadPool.execute(t);
                            }
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (event == EVENT.COMPLETED) {
                            Set<CodeDriverThread> tList = T_THREADLOCAL.get();
                            for (CodeDriverThread t : tList) {
                                CachedThreadPool.execute(t);
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
     * @Params: [参数, 事务提交后回调函数, 事务完全接受后回调函数（回滚也会触发）]
     * @Returns: void
     **/
    public synchronized void execute(T t, ICommitted<T> commited, ICompleted<T> completed, boolean isSync) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            if (commited != null) {
                if (!isSync) {
                    CachedThreadPool.execute(new CodeDriverThread(this.threadName + "-COMMITED") {
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
                    CachedThreadPool.execute(new CodeDriverThread(this.threadName + "COMPLETED") {
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
                                CachedThreadPool.execute(new CodeDriverThread(threadName + "-COMMITTED") {
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
                                CachedThreadPool.execute(new CodeDriverThread(threadName + "COMPLETED") {
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
