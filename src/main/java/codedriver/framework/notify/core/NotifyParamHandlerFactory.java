/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.notify.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;
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
                resultObj.put(param, handler.getText(object));
            }
        }
        return resultObj;
    }
    @Override
    protected void onInitialized(CodedriverWebApplicationContext context) {
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
