/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.crossover;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
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
    protected void onInitialized(NeatLogicWebApplicationContext context) {
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
