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

package neatlogic.framework.transaction.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.exception.core.ApiRuntimeException;
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

    public static class EscapeHandler extends NeatLogicThread {
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
                } else {
                    state.setException((ApiRuntimeException) ex);
                }
            } finally {
                latch.countDown();
            }
        }
    }


}
