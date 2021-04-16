/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.batch;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.transaction.util.TransactionUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Title: BatchRunner
 * @Package codedriver.framework.batch
 * @Description: 批量处理框架，支持根据列表按照指定并行度并发处理逻辑
 * @Author: chenqiwei
 * @Date: 2021/1/4 9:31 上午
 **/
public class BatchRunner<T> {
    private final static Logger logger = LoggerFactory.getLogger(BatchRunner.class);

    public void execute(List<T> itemList, int parallel, BatchJob<T> job) {
        if (CollectionUtils.isNotEmpty(itemList)) {
            parallel = Math.min(itemList.size(), parallel);
            CountDownLatch latch = new CountDownLatch(parallel);
            for (int i = 0; i < parallel; i++) {
                Runner<T> runner = new Runner<>(i, parallel, itemList, job, latch);
                CachedThreadPool.execute(runner);
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    static class Runner<T> extends CodeDriverThread {
        int index;
        int parallel;
        List<T> itemList;
        BatchJob<T> job;
        CountDownLatch latch;

        public Runner(int _index, int _parallel, List<T> _itemList, BatchJob<T> _job, CountDownLatch _latch) {
            index = _index;
            parallel = _parallel;
            itemList = _itemList;
            job = _job;
            latch = _latch;
        }

        @Override
        protected void execute() {
            try {
                for (int i = index; i < itemList.size(); i += parallel) {
                    TransactionStatus ts = TransactionUtil.openTx();
                    try {
                        job.execute(itemList.get(i));
                        TransactionUtil.commitTx(ts);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        TransactionUtil.rollbackTx(ts);
                    }
                }
            } finally {
                latch.countDown();
            }
        }
    }
}
