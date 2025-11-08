/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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

    public static <T extends ICrossoverService> T tryToGetApi(Class<? extends ICrossoverService> apiClass) {
        try {
            return getApi(apiClass);
        } catch (InnerApiNotFoundException e) {
            return null;
        }
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
