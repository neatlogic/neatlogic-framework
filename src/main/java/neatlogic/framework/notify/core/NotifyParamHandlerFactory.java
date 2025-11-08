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

package neatlogic.framework.notify.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linbq
 * @since 2021/10/15 16:39
 **/
@RootComponent
public class NotifyParamHandlerFactory extends ModuleInitializedListenerBase {

    private final Logger logger = LoggerFactory.getLogger(NotifyParamHandlerFactory.class);
    private static final Map<String, INotifyParamHandler> map = new HashMap<>();

    public static INotifyParamHandler getHandler(String handler) {
        return map.get(handler);
    }

    public static JSONObject getData(List<String> paramList, Object object, INotifyTriggerType notifyTriggerType) {
        JSONObject resultObj = new JSONObject();
        for (String param : paramList) {
            INotifyParamHandler handler = map.get(param);
            if (handler != null) {
                Object text = handler.getText(object, notifyTriggerType);
//                System.out.println(param + "-->" + JSONObject.toJSONString(text));
                resultObj.put(param, text);
            }
        }
        return resultObj;
    }
    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, INotifyParamHandler> myMap = context.getBeansOfType(INotifyParamHandler.class);
        for (Map.Entry<String, INotifyParamHandler> entry : myMap.entrySet()) {
            INotifyParamHandler paramHandler = entry.getValue();
            String value = paramHandler.getValue();
            if (StringUtils.isNotEmpty(value)) {
                if (map.containsKey(value)) {
                    logger.error("INotifyParamHandler '{}({})' repeat", paramHandler.getClass().getSimpleName(), value);
                    System.exit(1);
                }
                map.put(value, paramHandler);
            }
        }
    }

    @Override
    protected void myInit() {

    }

}
