package codedriver.framework.message.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.message.constvalue.PopUpType;
import codedriver.framework.message.dto.MessageHandlerVo;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Title: MessageHandlerFactory
 * @Package codedriver.framework.message.core
 * @Description: 消息处理器工厂类
 * @Author: linbq
 * @Date: 2020/12/30 15:36
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@RootComponent
public class MessageHandlerFactory extends ApplicationListenerBase {
    private static Map<String, IMessageHandler> messageHandlerMap = new HashMap<>();

    private static List<MessageHandlerVo> messageHandlerVoList = new ArrayList<>();

    @Override
    protected void myInit() {

    }

    public static IMessageHandler getHandler(String handler){
        return messageHandlerMap.get(handler);
    }

    public static List<MessageHandlerVo> getMessageHandlerVoList(){
        return messageHandlerVoList;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IMessageHandler> map = context.getBeansOfType(IMessageHandler.class);
        for(Map.Entry<String, IMessageHandler> entry : map.entrySet()){
            IMessageHandler messageHandler = entry.getValue();
            messageHandlerMap.put(messageHandler.getHandler(), messageHandler);

            MessageHandlerVo messageHandlerVo = new MessageHandlerVo();
            messageHandlerVo.setHandler(messageHandler.getHandler());
            messageHandlerVo.setDescription(messageHandler.getDescription());
            messageHandlerVo.setName(messageHandler.getName());
            messageHandlerVo.setIsActive(1);
            messageHandlerVo.setModuleId(context.getId());
            ModuleVo moduleVo = ModuleUtil.getModuleById(context.getId());
            messageHandlerVo.setModuleName(moduleVo.getGroupName());
            messageHandlerVo.setPopUp(messageHandler.getPopUp());
            messageHandlerVo.setPublic(messageHandler.isPublic());
            messageHandlerVoList.add(messageHandlerVo);
        }
    }
}
