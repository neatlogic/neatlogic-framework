/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.notify.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.CodedriverWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.dto.ValueTextVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class NotifyHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, INotifyHandler> notifyHandlerMap = new HashMap<>();

    private static final List<ValueTextVo> notifyHandlerTypeList = new ArrayList<>();

    private static final List<ValueTextVo> notifyHandlerNameList = new ArrayList<>();

    public static List<ValueTextVo> getNotifyHandlerTypeList() {
        return notifyHandlerTypeList;
    }

    public static List<ValueTextVo> getNotifyHandlerNameList() {
        return notifyHandlerNameList;
    }

    public static INotifyHandler getHandler(String handler) {
        return notifyHandlerMap.get(handler);
    }


    @Override
    protected void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, INotifyHandler> myMap = context.getBeansOfType(INotifyHandler.class);
        for (Map.Entry<String, INotifyHandler> entry : myMap.entrySet()) {
            INotifyHandler plugin = entry.getValue();
            if (plugin.getClassName() != null) {
                notifyHandlerMap.put(plugin.getClass().getSimpleName(), plugin);
                notifyHandlerTypeList.add(new ValueTextVo(plugin.getClass().getSimpleName(), plugin.getName()));
                notifyHandlerNameList.add(new ValueTextVo(plugin.getClass().getSimpleName(), plugin.getType()));
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
