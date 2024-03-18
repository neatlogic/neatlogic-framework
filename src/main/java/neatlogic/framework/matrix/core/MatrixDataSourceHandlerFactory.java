/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
