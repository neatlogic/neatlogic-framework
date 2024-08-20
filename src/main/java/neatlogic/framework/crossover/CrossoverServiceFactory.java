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

package neatlogic.framework.crossover;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
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
        return null;
        //throw new InnerApiNotFoundException(apiClass.getName());
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
