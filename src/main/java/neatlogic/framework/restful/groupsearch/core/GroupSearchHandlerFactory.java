/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.restful.groupsearch.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.CodedriverWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.exception.groupsearch.GroupSearchHandlerNotFoundException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class GroupSearchHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IGroupSearchHandler> componentMap = new HashMap<>();

    public static IGroupSearchHandler getHandler(String handler) {
        if (!componentMap.containsKey(handler) || componentMap.get(handler) == null) {
            throw new GroupSearchHandlerNotFoundException(handler);
        }
        return componentMap.get(handler);
    }

    public static Map<String, IGroupSearchHandler> getComponentMap() {
        return componentMap;
    }


    @Override
    protected void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IGroupSearchHandler> myMap = context.getBeansOfType(IGroupSearchHandler.class);
        for (Map.Entry<String, IGroupSearchHandler> entry : myMap.entrySet()) {
            IGroupSearchHandler component = entry.getValue();
            if (StringUtils.isNotBlank(component.getName())) {
                componentMap.put(component.getName(), component);
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
