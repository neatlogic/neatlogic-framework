/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.message.core;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.message.dto.MessageHandlerVo;
import neatlogic.framework.util.$;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class MessageHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IMessageHandler> messageHandlerMap = new HashMap<>();

    private static final List<MessageHandlerVo> messageHandlerVoList = new ArrayList<>();

    @Override
    protected void myInit() {

    }

    public static IMessageHandler getHandler(String handler) {
        return messageHandlerMap.get(handler);
    }

    public static List<MessageHandlerVo> getMessageHandlerVoList() {
        if(CollectionUtils.isNotEmpty(messageHandlerVoList)){
            List<MessageHandlerVo> messageHandlerVos = JSONArray.parseArray(JSONArray.toJSONString(messageHandlerVoList),MessageHandlerVo.class);
            messageHandlerVos.forEach(o-> {
                o.setModuleName($.t(o.getModuleName()));
                o.setName($.t(o.getName()));
                o.setDescription($.t(o.getDescription()));
            });
            return messageHandlerVos;
        }
        return messageHandlerVoList;
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
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
