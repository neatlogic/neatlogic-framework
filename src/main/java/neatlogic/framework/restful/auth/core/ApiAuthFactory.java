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
