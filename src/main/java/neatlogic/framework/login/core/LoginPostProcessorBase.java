/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
