/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
