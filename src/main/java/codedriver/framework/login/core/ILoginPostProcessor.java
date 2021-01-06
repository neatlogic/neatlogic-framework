package codedriver.framework.login.core;

import codedriver.framework.asynchronization.thread.CodeDriverThread;

/**
 * @Title: LoginPostProcessor
 * @Package codedriver.framework.login.core
 * @Description: 登录后处理器接口
 * @Author: linbq
 * @Date: 2021/1/6 14:53
 **/
public interface ILoginPostProcessor {
    public void loginAfterInitialization();
}
