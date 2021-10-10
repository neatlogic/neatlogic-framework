/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.transaction.core;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.exception.core.ApiRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class EscapeTransactionJob {
    private final static Logger logger = LoggerFactory.getLogger(EscapeTransactionJob.class);

    private final IEscapeTransaction thread;


    public EscapeTransactionJob(IEscapeTransaction _thread) {
        thread = _thread;
    }

    public State execute() {
        State state = new State();
        if (thread != null) {
            CountDownLatch latch = new CountDownLatch(1);
            CachedThreadPool.execute(new EscapeHandler(latch, thread, state));
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return state;
    }

    public static class State {
        private boolean isSucceed = false;
        private String error;
        private RuntimeException exception;

        public boolean isSucceed() {
            return isSucceed;
        }

        public void setSucceed(boolean succeed) {
            isSucceed = succeed;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public RuntimeException getException() {
            return exception;
        }

        public void setException(RuntimeException exception) {
            this.exception = exception;
        }
    }

    public static class EscapeHandler extends CodeDriverThread {
        private final IEscapeTransaction thread;
        private final CountDownLatch latch;
        private final State state;

        public EscapeHandler(CountDownLatch _latch, IEscapeTransaction _thread, State _state) {
            super("ESCAPE-TRANSACTION-HANDLER");
            thread = _thread;
            latch = _latch;
            state = _state;
        }

        @Override
        protected void execute() {
            try {
                thread.execute();
                state.setSucceed(true);
            } catch (Exception ex) {
                state.setSucceed(false);
                state.setError(ex.getMessage());
                if (!(ex instanceof ApiRuntimeException)) {
                    logger.error(ex.getMessage(), ex);
                    state.setException(new RuntimeException(ex));
                }
            } finally {
                latch.countDown();
            }
        }
    }


}
