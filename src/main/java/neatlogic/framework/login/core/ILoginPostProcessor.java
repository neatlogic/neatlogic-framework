package neatlogic.framework.login.core;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;

/**
 * @Title: LoginPostProcessor
 * @Package neatlogic.framework.login.core
 * @Description: 登录后处理器接口
 * @Author: linbq
 * @Date: 2021/1/6 14:53
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public interface ILoginPostProcessor {
    public void loginAfterInitialization();
}
