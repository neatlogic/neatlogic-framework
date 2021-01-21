package codedriver.framework.asynchronization.threadpool;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: TransactionSynchronization
 * @Package codedriver.framework.asynchronization.thread
 * @Description: 事务同步线程池
 * @Author: linbq
 * @Date: 2021/1/20 18:14
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class TransactionSynchronizationPool {

    private static ThreadLocal<List<CodeDriverThread>> threadLocal = new ThreadLocal<>();

    public static void execute(CodeDriverThread thread){
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            /**
             * 如果当前存在事务，先将任务存放在threadLocal中，等待当前事务提交成功再将任务放入线程池
             **/
            List<CodeDriverThread> runableList = threadLocal.get();
            if (runableList == null) {
                runableList = new ArrayList<>();
                threadLocal.set(runableList);
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                    /**
                     * @Description: 当前事务提交成功，才执行此方法中的逻辑
                     * @Author: linbq
                     * @Date: 2021/1/21 14:39
                     * @Params:[]
                     * @Returns:void
                     **/
                    @Override
                    public void afterCommit() {
                        List<CodeDriverThread> runableList = threadLocal.get();
                        for (CodeDriverThread runnable : runableList) {
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
            runableList.add(thread);
        } else {
            /**
             * 如果当前不存在事务，直接将任务存任务放入线程池
             **/
            CachedThreadPool.execute(thread);
        }
    }
}
