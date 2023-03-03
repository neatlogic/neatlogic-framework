/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
