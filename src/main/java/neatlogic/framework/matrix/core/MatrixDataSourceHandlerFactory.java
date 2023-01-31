/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.matrix.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.CodedriverWebApplicationContext;
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
    protected void onInitialized(CodedriverWebApplicationContext context) {
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
