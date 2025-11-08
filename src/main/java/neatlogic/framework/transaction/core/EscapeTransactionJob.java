/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.transaction.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.exception.core.ApiRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class EscapeTransactionJob {
    private static final Logger logger = LoggerFactory.getLogger(EscapeTransactionJob.class);

    private final IEscapeTransaction thread;


    public EscapeTransactionJob(IEscapeTransaction _thread) {
        thread = _thread;
    }

    public State execute() {

        EscapeTransactionJob.State state = new EscapeTransactionJob.State();
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
        private final
        EscapeTransactionJob.State state;

        public EscapeHandler(CountDownLatch _latch, IEscapeTransaction _thread, EscapeTransactionJob.State _state) {
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
                    state.setException(new RuntimeException(ex));
                } else {
                    state.setException((ApiRuntimeException) ex);
                }
                logger.error(ex.getMessage(), ex);
            } finally {
                latch.countDown();
            }
        }
    }


}
