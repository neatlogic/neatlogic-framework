/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.initialdata.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class AfterInitialDataImportHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IAfterInitialDataImportHandler> componentMap = new HashMap<>();

    @Override
    protected void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IAfterInitialDataImportHandler> myMap = context.getBeansOfType(IAfterInitialDataImportHandler.class);
        for (Map.Entry<String, IAfterInitialDataImportHandler> entry : myMap.entrySet()) {
            IAfterInitialDataImportHandler component = entry.getValue();
            componentMap.put(component.getModuleId(), component);
        }
    }

    public static IAfterInitialDataImportHandler getHandler(String moduleId) {
        return componentMap.get(moduleId);
    }

    @Override
    protected void myInit() {

    }
}
