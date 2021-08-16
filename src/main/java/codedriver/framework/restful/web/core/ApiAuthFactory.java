/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.web.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class ApiAuthFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IApiAuth> apiAuthMap = new HashMap<>();

    public static Map<String, IApiAuth> getApiAuthMap() {
        return apiAuthMap;
    }

    public static final IApiAuth getApiAuth(String type) {
        return apiAuthMap.get(type.toUpperCase());
    }

    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, IApiAuth> myMap = context.getBeansOfType(IApiAuth.class);
        for (Map.Entry<String, IApiAuth> entry : myMap.entrySet()) {
            IApiAuth apiAuth = entry.getValue();
            apiAuthMap.put(apiAuth.getType().toUpperCase(), apiAuth);
        }

    }

    @Override
    protected void myInit() {
        // TODO Auto-generated method stub

    }

}
