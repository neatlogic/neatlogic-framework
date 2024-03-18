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

    private Logger logger = LoggerFactory.getLogger(NotifyParamHandlerFactory.class);
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
