package codedriver.framework.reminder.core;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import codedriver.framework.common.RootComponent;
import codedriver.framework.reminder.dto.GlobalReminderHandlerVo;

@RootComponent
public class GlobalReminderHandlerFactory implements ApplicationListener<ContextRefreshedEvent> {

	private static List<GlobalReminderHandlerVo> reminderHandlerList = new ArrayList<>();
	private static Map<String, IGlobalReminderHandler> reminderMap = new HashMap<>();
	private static Map<String, GlobalReminderHandlerVo> reminderHandlerMap = new HashMap<>();

	public static IGlobalReminderHandler getReminder(String name) {
		if (!reminderMap.containsKey(name)) {
			throw new RuntimeException("找不到名称为：" + name +" 的实时动态插件");
		}
		return reminderMap.get(name);
	}

	public static List<GlobalReminderHandlerVo> getReminderHandlerList(){
		return reminderHandlerList;
	}

	public static Map<String, GlobalReminderHandlerVo> getReminderHandlerMap(){
		return reminderHandlerMap;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IGlobalReminderHandler> map = context.getBeansOfType(IGlobalReminderHandler.class);
		for (Map.Entry<String, IGlobalReminderHandler> entry : map.entrySet()) {
			IGlobalReminderHandler reminder = entry.getValue();
			GlobalReminderHandlerVo reminderHandlerVo = new GlobalReminderHandlerVo();
			reminderHandlerVo.setName(reminder.getName());
			reminderHandlerVo.setParamList(reminder.getConfig());
			reminderHandlerVo.setModuleId(context.getId());
			reminderHandlerVo.setDescription(reminder.getDescription());
			reminderHandlerVo.setHandler(reminder.getHandler());
			reminderHandlerList.add(reminderHandlerVo);
			reminderMap.put(reminder.getHandler(), reminder);
			reminderHandlerMap.put(reminder.getHandler(), reminderHandlerVo);
		}
	}
}
