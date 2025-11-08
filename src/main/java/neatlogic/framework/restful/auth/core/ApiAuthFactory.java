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

package neatlogic.framework.restful.auth.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class ApiAuthFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IApiAuth> apiAuthMap = new HashMap<>();

    public static Map<String, IApiAuth> getApiAuthMap() {
        return apiAuthMap;
    }

    public static IApiAuth getApiAuth(String type) {
        return apiAuthMap.get(type.toUpperCase());
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
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
