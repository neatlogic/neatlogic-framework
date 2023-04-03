/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.message.core;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.message.dto.MessageHandlerVo;
import neatlogic.framework.util.I18nUtils;
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
                o.setModuleName(I18nUtils.getMessage(o.getModuleName()));
                o.setName(I18nUtils.getMessage(o.getName()));
                o.setDescription(I18nUtils.getMessage(o.getDescription()));
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
