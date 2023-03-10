/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.matrix.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author linbq
 * @since 2021/11/4 18:13
 **/
@RootComponent
public class MatrixDataSourceHandlerFactory extends ModuleInitializedListenerBase {

    private static Map<String, IMatrixDataSourceHandler> map = new HashMap<>();

    public static IMatrixDataSourceHandler getHandler(String handler) {
        return map.get(handler);
    }

    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IMatrixDataSourceHandler> myMap = context.getBeansOfType(IMatrixDataSourceHandler.class);
        for (Map.Entry<String, IMatrixDataSourceHandler> entry : myMap.entrySet()) {
            IMatrixDataSourceHandler matrixDataSourceHandler = entry.getValue();
            map.put(matrixDataSourceHandler.getHandler(), matrixDataSourceHandler);
        }
    }

    @Override
    protected void myInit() {

    }
}
