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
