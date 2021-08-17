package codedriver.framework.notify.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;
import codedriver.framework.common.dto.ValueTextVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 10:04
 **/
@RootComponent
public class NotifyHandlerFactory extends ModuleInitializedListenerBase {
	private static final Map<String, INotifyHandler> notifyHandlerMap = new HashMap<>();

	private static final List<ValueTextVo> notifyHandlerTypeList = new ArrayList<>();

	private static final List<ValueTextVo> notifyHandlerNameList = new ArrayList<>();

	public static List<ValueTextVo> getNotifyHandlerTypeList() {
		return notifyHandlerTypeList;
	}

	public static List<ValueTextVo> getNotifyHandlerNameList() {
		return notifyHandlerNameList;
	}

	public static INotifyHandler getHandler(String handler) {
		return notifyHandlerMap.get(handler);
	}

	@Override
	protected void onInitialized(CodedriverWebApplicationContext context) {
		Map<String, INotifyHandler> myMap = context.getBeansOfType(INotifyHandler.class);
		for (Map.Entry<String, INotifyHandler> entry : myMap.entrySet()) {
			INotifyHandler plugin = entry.getValue();
			if (plugin.getClassName() != null) {
				notifyHandlerMap.put(plugin.getClassName(), plugin);
				notifyHandlerTypeList.add(new ValueTextVo(plugin.getClassName(), plugin.getName()));
				notifyHandlerNameList.add(new ValueTextVo(plugin.getClassName(), plugin.getType()));
			}
		}
	}

	@Override
	protected void myInit() {

	}
}
