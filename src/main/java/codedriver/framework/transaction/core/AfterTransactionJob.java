/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
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
    private final ThreadLocal<Set<T>> THREADLOCAL = new ThreadLocal<>();

    public void execute(T t, ICommitted<T> commited) {
        execute(t, commited, null);
    }

    /*
     * @Description:
     * @Author: chenqiwei
     * @Date: 2021/1/7 4:06 下午
     * @Params: [参数, 事务提交后回调函数, 事务完全接受后回调函数（回滚也会触发）]
     * @Returns: void
     **/
    public synchronized void execute(T t, ICommitted<T> commited, ICompleted<T> completed) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            if (commited != null) {
                commited.execute(t);
            }
            if (completed != null) {
                completed.execute(t);
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
                            Set<T> tList = (Set<T>) THREADLOCAL.get();
                            CachedThreadPool.execute(new CodeDriverThread() {
                                @Override
                                protected void execute() {
                                    for (T t : tList) {
                                        commited.execute(t);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void afterCompletion(int status) {
                        if (completed != null) {
                            Set<T> tList = (Set<T>) THREADLOCAL.get();
                            CachedThreadPool.execute(new CodeDriverThread() {
                                @Override
                                protected void execute() {
                                    for (T t : tList) {
                                        completed.execute(t);
                                    }
                                }
                            });
                        }
                        THREADLOCAL.remove();
                    }
                });
            }
            tList.add(t);
        }
    }
}
