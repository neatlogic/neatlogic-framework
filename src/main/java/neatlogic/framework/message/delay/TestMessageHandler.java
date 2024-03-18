package neatlogic.framework.message.delay;

import neatlogic.framework.message.core.MessageHandlerBase;
import neatlogic.framework.notify.dto.NotifyVo;

import java.util.List;

/**
 * @Title: TestMessageHandler
 * @Package neatlogic.framework.message.delay
 * @Description: 消息测试类型，用于压测
 * @Author: linbq
 * @Date: 2021/1/12 7:55

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

    @Override
    public boolean getNeedCompression() {
        return false;
    }

    @Override
    public NotifyVo compress(List<NotifyVo> notifyVoList) {
        return null;
    }
}
