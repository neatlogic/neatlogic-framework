/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
