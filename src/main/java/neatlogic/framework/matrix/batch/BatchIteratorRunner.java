/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.matrix.batch;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.transaction.util.TransactionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 批量处理
 *
 * @author lvzk
 * @since 2024/6/3 10:31 上午
 **/
public class BatchIteratorRunner<T> {
    private final static Logger logger = LoggerFactory.getLogger(BatchIteratorRunner.class);

    public static class State {
        private boolean isSucceed = false;
        private Exception exception;

        public boolean isSucceed() {
            return isSucceed;
        }

        public void setSucceed(boolean succeed) {
            isSucceed = succeed;
        }


        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

        public String getExceptionMsg() {
            if(exception != null){
                return exception.getMessage();
            }
            return null;
        }

    }

    /**
     * @param job 执行函数
     */
    public State execute(List<Iterator<T>> iteratorList, BatchIteratorJob<T> job, String threadName) throws InterruptedException {
        return execute(iteratorList, false, job, threadName);
    }

    /**
     * @param needTransaction 每个对象的执行过程是否需要启用事务
     * @param job             执行函数
     */
    public State execute(List<Iterator<T>> iteratorList, boolean needTransaction, BatchIteratorJob<T> job, String threadName) throws InterruptedException {
        State state = new State();
        if (!iteratorList.isEmpty()) {
            //状态默认是成功状态，任意线程出现异常则置为失败
            state.setSucceed(true);
            CountDownLatch latch = new CountDownLatch(iteratorList.size());

            for (Iterator<T> iterator : iteratorList) {
                Runner<T> runner = new Runner<>(threadName, needTransaction, iterator, job, latch, state);
                CachedThreadPool.execute(runner);
            }
            latch.await();
        }
        return state;
    }


    static class Runner<T> extends NeatLogicThread {
        Iterator<T> iterator;
        BatchIteratorJob<T> job;
        CountDownLatch latch;
        boolean needTransaction;
        State state;
        List<T> rowList = new ArrayList<>();

        public Runner(String _threadName, boolean _needTransaction, Iterator<T> _iterator, BatchIteratorJob<T> _job, CountDownLatch _latch, State _state) {
            super(_threadName);
            iterator = _iterator;
            job = _job;
            latch = _latch;
            needTransaction = _needTransaction;
            state = _state;
        }

        @Override
        protected void execute() {
            try {
                while (iterator.hasNext()) {
                    executeJob(iterator, rowList, needTransaction, job, state);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
                state.setSucceed(false);
                state.setException(ex);
            } finally {
                latch.countDown();
            }
        }

        private void executeJob(Iterator<T> item, List<T> rowList, boolean needTransaction, BatchIteratorJob<T> job, State state) {
            TransactionStatus ts = null;
            if (needTransaction) {
                ts = TransactionUtil.openTx();
            }
            try {
                job.execute(item, rowList);
                if (ts != null) {
                    TransactionUtil.commitTx(ts);
                }
            } catch (ApiRuntimeException e) {
                state.setSucceed(false);
                state.setException(e);
                logger.warn(e.getMessage(), e);
                if (ts != null) {
                    TransactionUtil.rollbackTx(ts);
                }
            } catch (Exception e) {
                state.setSucceed(false);
                state.setException(e);
                logger.error(e.getMessage(), e);
                if (ts != null) {
                    TransactionUtil.rollbackTx(ts);
                }
            }
        }
    }
}
