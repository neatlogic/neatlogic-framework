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

package neatlogic.framework.login.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;

public abstract class LoginPostProcessorBase implements ILoginPostProcessor {
    @Override
    public void loginAfterInitialization() {
        CachedThreadPool.execute(new LoginPostProcessorThread(this));
    }

    protected abstract void myLoginAfterInitialization();

    private static class LoginPostProcessorThread extends NeatLogicThread {

        private LoginPostProcessorBase loginPostProcessor;

        public LoginPostProcessorThread(LoginPostProcessorBase loginPostProcessor) {
            super("LOGIN-POST-PROCESSOR-" + UserContext.get().getUserUuid(true));
            this.loginPostProcessor = loginPostProcessor;
        }

        @Override
        protected void execute() {
            loginPostProcessor.myLoginAfterInitialization();
        }
    }
}
