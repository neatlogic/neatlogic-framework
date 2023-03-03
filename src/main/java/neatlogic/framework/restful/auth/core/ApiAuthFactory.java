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
