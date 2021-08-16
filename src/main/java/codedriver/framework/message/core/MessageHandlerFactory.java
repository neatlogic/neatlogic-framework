/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.message.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.message.dto.MessageHandlerVo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class MessageHandlerFactory extends ApplicationListenerBase {
    private static Map<String, IMessageHandler> messageHandlerMap = new HashMap<>();

    private static List<MessageHandlerVo> messageHandlerVoList = new ArrayList<>();

    @Override
    protected void myInit() {

    }

    public static IMessageHandler getHandler(String handler) {
        return messageHandlerMap.get(handler);
    }

    public static List<MessageHandlerVo> getMessageHandlerVoList() {
        return messageHandlerVoList;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext c = event.getApplicationContext();
        if (c instanceof CodedriverWebApplicationContext) {
            CodedriverWebApplicationContext context = (CodedriverWebApplicationContext) c;
            Map<String, IMessageHandler> map = context.getBeansOfType(IMessageHandler.class);
            for (Map.Entry<String, IMessageHandler> entry : map.entrySet()) {
                IMessageHandler messageHandler = entry.getValue();
                messageHandlerMap.put(messageHandler.getHandler(), messageHandler);

                MessageHandlerVo messageHandlerVo = new MessageHandlerVo();
                messageHandlerVo.setHandler(messageHandler.getHandler());
                messageHandlerVo.setDescription(messageHandler.getDescription());
                messageHandlerVo.setName(messageHandler.getName());
                messageHandlerVo.setIsActive(1);
                messageHandlerVo.setModuleId(context.getId());
                messageHandlerVo.setModuleName(context.getGroupName());
                messageHandlerVo.setPopUp(messageHandler.getPopUp());
                messageHandlerVo.setPublic(messageHandler.isPublic());
                messageHandlerVoList.add(messageHandlerVo);
            }
        }
    }
}
