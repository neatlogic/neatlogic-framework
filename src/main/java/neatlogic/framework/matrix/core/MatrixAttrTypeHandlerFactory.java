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

package neatlogic.framework.matrix.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lvzk
 * @since 2024/05/24 18:13
 **/
@RootComponent
public class MatrixAttrTypeHandlerFactory extends ModuleInitializedListenerBase {

    private static Map<String, IMatrixAttrType> map = new HashMap<>();

    public static IMatrixAttrType getHandler(String handler) {
        return map.get(handler);
    }

    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IMatrixAttrType> myMap = context.getBeansOfType(IMatrixAttrType.class);
        for (Map.Entry<String, IMatrixAttrType> entry : myMap.entrySet()) {
            IMatrixAttrType matrixAttrTypeHandler = entry.getValue();
            map.put(matrixAttrTypeHandler.getHandler(), matrixAttrTypeHandler);
        }
    }

    @Override
    protected void myInit() {

    }
}
