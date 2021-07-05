/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.asynchronization.threadpool;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

public class TransactionSynchronizationPool {

    private static final ThreadLocal<List<CodeDriverThread>> threadLocal = new ThreadLocal<>();

    public static void execute(CodeDriverThread thread) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            /*
              如果当前存在事务，先将任务存放在threadLocal中，等待当前事务提交成功再将任务放入线程池
             */
            List<CodeDriverThread> runnableList = threadLocal.get();
            if (runnableList == null) {
                runnableList = new ArrayList<>();
                threadLocal.set(runnableList);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    /**
                     * 当前事务提交成功，才执行此方法中的逻辑
                     */
                    @Override
                    public void afterCommit() {
                        List<CodeDriverThread> runnableList = threadLocal.get();
                        for (CodeDriverThread runnable : runnableList) {
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
}
