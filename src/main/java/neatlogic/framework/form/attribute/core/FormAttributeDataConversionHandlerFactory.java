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

package neatlogic.framework.form.attribute.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class FormAttributeDataConversionHandlerFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IFormAttributeDataConversionHandler> handlerMap = new HashMap<>();

    public static IFormAttributeDataConversionHandler getHandler(String type) {
        return handlerMap.get(type);
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IFormAttributeDataConversionHandler> myMap = context.getBeansOfType(IFormAttributeDataConversionHandler.class);
        for (Map.Entry<String, IFormAttributeDataConversionHandler> entry : myMap.entrySet()) {
            IFormAttributeDataConversionHandler handler = entry.getValue();
            if (handler.getHandler() != null) {
                if (handlerMap.containsKey(handler.getHandler())) {
                    System.err.println("表单插件：" + handler.getHandler() + "已存在，请检查代码");
                } else {
                    handlerMap.put(handler.getHandler(), handler);
                }
            }
        }
    }

    @Override
    protected void myInit() {

    }
}
