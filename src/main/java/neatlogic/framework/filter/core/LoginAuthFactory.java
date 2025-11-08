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

package neatlogic.framework.filter.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class LoginAuthFactory extends ModuleInitializedListenerBase {
    private static final Map<String, ILoginAuthHandler> loginAuthMap = new HashMap<>();


    public static ILoginAuthHandler getLoginAuth(String type) {
        return loginAuthMap.get(type.toUpperCase());
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, ILoginAuthHandler> myMap = context.getBeansOfType(ILoginAuthHandler.class);
        for (Map.Entry<String, ILoginAuthHandler> entry : myMap.entrySet()) {
            ILoginAuthHandler authAuth = entry.getValue();
            loginAuthMap.put(authAuth.getType().toUpperCase(), authAuth);
        }

    }

    @Override
    protected void myInit() {
        // TODO Auto-generated method stub

    }

}
