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

package neatlogic.framework.notify.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import com.alibaba.fastjson.JSONObject;
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

    private Logger logger = LoggerFactory.getLogger(NotifyParamHandlerFactory.class);
    private static final Map<String, INotifyParamHandler> map = new HashMap<>();

    public static INotifyParamHandler getHandler(String handler) {
        return map.get(handler);
    }

    public static JSONObject getData(List<String> paramList, Object object) {
        JSONObject resultObj = new JSONObject();
        for (String param : paramList) {
            INotifyParamHandler handler = map.get(param);
            if (handler != null) {
                Object text = handler.getText(object);
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
                    logger.error("INotifyParamHandler '" + paramHandler.getClass().getSimpleName()+ "(" + value + ")' repeat");
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
