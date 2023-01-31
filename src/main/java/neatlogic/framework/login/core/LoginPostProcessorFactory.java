/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.login.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.CodedriverWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RootComponent
public class LoginPostProcessorFactory extends ModuleInitializedListenerBase {

    private static Set<ILoginPostProcessor> loginPostProcessorSet = new HashSet<>();

    public static Set<ILoginPostProcessor> getLoginPostProcessorSet() {
        return loginPostProcessorSet;
    }

    @Override
    protected void myInit() {

    }

    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, ILoginPostProcessor> map = context.getBeansOfType(ILoginPostProcessor.class);
        for (Map.Entry<String, ILoginPostProcessor> entry : map.entrySet()) {
            loginPostProcessorSet.add(entry.getValue());
        }
    }
}
