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

package neatlogic.framework.login.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
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
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, ILoginPostProcessor> map = context.getBeansOfType(ILoginPostProcessor.class);
        for (Map.Entry<String, ILoginPostProcessor> entry : map.entrySet()) {
            loginPostProcessorSet.add(entry.getValue());
        }
    }
}
