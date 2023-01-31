/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.auditconfig.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.CodedriverWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class AuditCleanerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IAuditCleaner> cleanerMap = new HashMap<>();

    @Override
    protected void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IAuditCleaner> myMap = context.getBeansOfType(IAuditCleaner.class);
        for (Map.Entry<String, IAuditCleaner> entry : myMap.entrySet()) {
            IAuditCleaner component = entry.getValue();
            cleanerMap.put(component.getName(), component);
        }
    }

    public static IAuditCleaner getCleaner(String name) {
        return cleanerMap.get(name);
    }

    @Override
    protected void myInit() {

    }
}
