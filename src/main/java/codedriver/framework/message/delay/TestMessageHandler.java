package codedriver.framework.message.delay;

import codedriver.framework.message.core.MessageHandlerBase;

/**
 * @Title: TestMessageHandler
 * @Package codedriver.framework.message.delay
 * @Description: TODO
 * @Author: linbq
 * @Date: 2021/1/12 7:55
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class TestMessageHandler extends MessageHandlerBase {
    @Override
    public String getName() {
        return "测试类型";
    }

    @Override
    public String getDescription() {
        return "用于测试消息缓存";
    }
}
