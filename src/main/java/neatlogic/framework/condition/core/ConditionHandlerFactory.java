/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.condition.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.CodedriverWebApplicationContext;
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
	public void onInitialized(CodedriverWebApplicationContext context) {
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
