/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.crossover;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.CodedriverWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.exception.innerapi.InnerApiNotFoundException;
import org.apache.commons.collections4.MapUtils;
import org.springframework.aop.support.AopUtils;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class CrossoverServiceFactory extends ModuleInitializedListenerBase {
    private static final Map<Class<? extends ICrossoverService>, ICrossoverService> apiMap = new HashMap<>();

    public static <T extends ICrossoverService> T getApi(Class<? extends ICrossoverService> apiClass) {
        if (MapUtils.isNotEmpty(apiMap)) {
            for (Class<? extends ICrossoverService> k : apiMap.keySet()) {
                if (apiClass.isAssignableFrom(k)) {
                    return (T) apiMap.get(k);
                }
            }
        }
        throw new InnerApiNotFoundException(apiClass.getName());
    }


    @Override
    protected void onInitialized(CodedriverWebApplicationContext context) {
        Map<String, ICrossoverService> myMap = context.getBeansOfType(ICrossoverService.class);
        for (Map.Entry<String, ICrossoverService> entry : myMap.entrySet()) {
            ICrossoverService component = entry.getValue();
            apiMap.put((Class<? extends ICrossoverService>) AopUtils.getTargetClass(component), component);
        }
    }

    @Override
    protected void myInit() {

    }
}
