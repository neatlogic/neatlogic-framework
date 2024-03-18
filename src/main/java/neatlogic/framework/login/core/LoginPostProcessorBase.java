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
