package codedriver.framework.login.core;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.asynchronization.threadpool.CommonThreadPool;

/**
 * @Title: LoginPostProcessorBase
 * @Package codedriver.framework.login.core
 * @Description: 登录后处理器基类
 * @Author: linbq
 * @Date: 2021/1/6 15:26
 **/
public abstract class LoginPostProcessorBase implements ILoginPostProcessor{
    @Override
    public void loginAfterInitialization() {
        CommonThreadPool.execute(new LoginPostProcessorThread(this));
    }

    protected abstract void myLoginAfterInitialization();

    private static class LoginPostProcessorThread extends CodeDriverThread {

        private LoginPostProcessorBase loginPostProcessor;

        public LoginPostProcessorThread(LoginPostProcessorBase loginPostProcessor){
            this.loginPostProcessor = loginPostProcessor;
            this.setThreadName("LOGIN-POST-PROCESSOR-" + UserContext.get().getUserUuid(true));
        }

        @Override
        protected void execute() {
            loginPostProcessor.myLoginAfterInitialization();
        }
    }
}
