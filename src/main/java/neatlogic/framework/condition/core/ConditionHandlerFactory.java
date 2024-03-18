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

package neatlogic.framework.condition.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @ClassName: ConditionHandlerFactory
 * @Description: 条件处理器工厂类
 */
@RootComponent
public class ConditionHandlerFactory extends ModuleInitializedListenerBase {

	private static final Map<String, IConditionHandler> conditionHandlerMap = new HashMap<>();

	private static final List<IConditionHandler> conditionHandlerList = new ArrayList<>();

	public static IConditionHandler getHandler(String name) {
		return conditionHandlerMap.get(name);
	}

	public static List<IConditionHandler> getConditionHandlerList() {
		return conditionHandlerList;
	}
	
	@Override
	public void onInitialized(NeatLogicWebApplicationContext context) {
		Map<String, IConditionHandler> myMap = context.getBeansOfType(IConditionHandler.class);
		for (Entry<String, IConditionHandler> entry : myMap.entrySet()) {
			IConditionHandler conditionHandler = entry.getValue();
			conditionHandlerMap.put(conditionHandler.getName(), conditionHandler);
			conditionHandlerList.add(conditionHandler);
		}
	}

	@Override
	protected void myInit() {
		// TODO Auto-generated method stub
		
	}

}
