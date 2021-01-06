package codedriver.framework.notify.handler;

import codedriver.framework.message.core.IMessageHandler;
import codedriver.framework.message.core.MessageHandlerFactory;
import codedriver.framework.notify.core.NotifyHandlerBase;
import codedriver.framework.notify.core.NotifyHandlerType;
import codedriver.framework.notify.dto.NotifyVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * @Title: MessageNotifyHandler
 * @Package codedriver.framework.notify.handler
 * @Description: 消息通知处理器
 * @Author: linbq
 * @Date: 2020/12/31 15:19
 **/
@Component
public class MessageNotifyHandler extends NotifyHandlerBase {

    @Override
    protected void myExecute(NotifyVo notifyVo) {
        if (CollectionUtils.isNotEmpty(notifyVo.getToUserUuidList()) || CollectionUtils.isNotEmpty(notifyVo.getToTeamUuidList()) || CollectionUtils.isNotEmpty(notifyVo.getToRoleUuidList())) {
            IMessageHandler handler = MessageHandlerFactory.getHandler(notifyVo.getMessageHandlerClass().getName());
            handler.send(notifyVo);
        }
    }

    @Override
    public String getName() {
        return NotifyHandlerType.REMIND.getText();
    }

    @Override
    public String getType() {
        return NotifyHandlerType.REMIND.getValue();
    }
}
