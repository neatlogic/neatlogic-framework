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
import java.util.Map.Entry;

@RootComponent
public class NotifyContentHandlerFactory extends ModuleInitializedListenerBase {

    private static final List<ValueTextVo> notifyContentHandlerList = new ArrayList<>();

    private static final Map<String, INotifyContentHandler> notifyContentHandlerMap = new HashMap<>();

    public static INotifyContentHandler getHandler(String handler) {
        return notifyContentHandlerMap.get(handler);
    }

    public static List<ValueTextVo> getNotifyContentHandlerList() {
        return notifyContentHandlerList;
    }

    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, INotifyContentHandler> map = context.getBeansOfType(INotifyContentHandler.class);
        for (Entry<String, INotifyContentHandler> entry : map.entrySet()) {
            notifyContentHandlerMap.put(entry.getValue().getClassName(), entry.getValue());
            notifyContentHandlerList.add(new ValueTextVo(entry.getValue().getClassName(), entry.getValue().getName()));
        }
    }

    @Override
    protected void myInit() {

    }

}
