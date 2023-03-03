/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.asynchronization.threadpool;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

public class TransactionSynchronizationPool {

    private static final ThreadLocal<List<NeatLogicThread>> threadLocal = new ThreadLocal<>();
    private static final ThreadLocal<List<NeatLogicThread>> threadLocalAfterRollback = new ThreadLocal<>();

    public static void execute(NeatLogicThread thread) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            /*
              如果当前存在事务，先将任务存放在threadLocal中，等待当前事务提交成功再将任务放入线程池
             */
            List<NeatLogicThread> runnableList = threadLocal.get();
            if (runnableList == null) {
                runnableList = new ArrayList<>();
                threadLocal.set(runnableList);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    /**
                     * 当前事务提交成功，才执行此方法中的逻辑
                     */
                    @Override
                    public void afterCommit() {
                        List<NeatLogicThread> runnableList = threadLocal.get();
                        for (NeatLogicThread runnable : runnableList) {
                            CachedThreadPool.execute(runnable);
                        }
                    }

                    /**
                     * @Description: 当前事务提交成功时status=0，当前事务回滚时status=1
                     * @Author: linbq
                     * @Date: 2021/1/21 14:40
                     * @Params:[status]
                     * @Returns:void
                     **/
                    @Override
                    public void afterCompletion(int status) {
                        threadLocal.remove();
                    }


                });
            }
            runnableList.add(thread);
        } else {
            /*
              如果当前不存在事务，直接将任务存任务放入线程池
             */
            CachedThreadPool.execute(thread);
        }
    }


    public static void executeAfterRollback(NeatLogicThread thread) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            /*
              如果当前存在事务，先将任务存放在threadLocal中，等待当前事务提交成功再将任务放入线程池
             */
            List<NeatLogicThread> rollbackRunnableList = threadLocalAfterRollback.get();
            if (rollbackRunnableList == null) {
                rollbackRunnableList = new ArrayList<>();
                threadLocalAfterRollback.set(rollbackRunnableList);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if(status == 1){
                            List<NeatLogicThread> rollbackRunnableList = threadLocalAfterRollback.get();
                            for (NeatLogicThread rollbackRunnable : rollbackRunnableList) {
                                CachedThreadPool.execute(rollbackRunnable);
                            }
                        }
                        threadLocalAfterRollback.remove();
                    }
                });
            }
            rollbackRunnableList.add(thread);
        } else {
            /*
              如果当前不存在事务，直接将任务存任务放入线程池
             */
            CachedThreadPool.execute(thread);
        }
    }
}
