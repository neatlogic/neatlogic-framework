package codedriver.framework.batch;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Title: BatchRunner
 * @Package codedriver.framework.batch
 * @Description: TODO
 * @Author: chenqiwei
 * @Date: 2021/1/4 9:31 上午
 **/
public class BatchRunner<T> {
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
                e.printStackTrace();
            }
        }
    }

    static class Runner<T> extends CodeDriverThread {
        int index = 0;
        int parallel = 0;
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
                    job.execute(itemList.get(i));
                }
            } finally {
                latch.countDown();
            }
        }
    }


}
