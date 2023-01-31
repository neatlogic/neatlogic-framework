/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.form.attribute.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.CodedriverWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.*;

@RootComponent
public class FormAttributeDataConversionHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IFormAttributeDataConversionHandler> handlerMap = new HashMap<>();

    public static IFormAttributeDataConversionHandler getHandler(String type) {
        return handlerMap.get(type);
    }

    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IFormAttributeDataConversionHandler> myMap = context.getBeansOfType(IFormAttributeDataConversionHandler.class);
        for (Map.Entry<String, IFormAttributeDataConversionHandler> entry : myMap.entrySet()) {
            IFormAttributeDataConversionHandler handler = entry.getValue();
            if (handler.getHandler() != null) {
                if (handlerMap.containsKey(handler.getHandler())) {
                    System.err.println("表单插件：" + handler.getHandler() + "已存在，请检查代码");
                } else {
                    handlerMap.put(handler.getHandler(), handler);
                }
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
