package codedriver.framework.reminder.core;


import codedriver.framework.common.RootComponent;
import codedriver.framework.reminder.dto.GlobalReminderVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class GlobalReminderFactory implements ApplicationListener<ContextRefreshedEvent> {

	private static List<GlobalReminderVo> reminderVoList = new ArrayList<>();
	private static Map<String, IGlobalReminder> reminderMap = new HashMap<>();
	private static Map<String, GlobalReminderVo> reminderVoMap = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(GlobalReminderFactory.class);

	public static IGlobalReminder getReminder(String name) {
		if (!reminderMap.containsKey(name)) {
			throw new RuntimeException("找不到名称为：" + name +" 的实时动态插件");
		}
		return reminderMap.get(name);
	}

	public static List<GlobalReminderVo> getReminderVoList(){
		return reminderVoList;
	}

	public static Map<String, GlobalReminderVo> getReminderVoMap(){
		return reminderVoMap;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IGlobalReminder> map = context.getBeansOfType(IGlobalReminder.class);
		for (Map.Entry<String, IGlobalReminder> entry : map.entrySet()) {
			IGlobalReminder reminder = entry.getValue();
			GlobalReminderVo reminderVo = new GlobalReminderVo();
			reminderVo.setName(reminder.getName());
			reminderVo.setReminderParamList(reminder.getConfig());
			reminderVo.setModuleId(context.getId());
			reminderVo.setDescription(reminder.getDescription());
			reminderVo.setPluginId(reminder.getPluginId());
			reminderVoList.add(reminderVo);
			reminderMap.put(reminder.getPluginId(), reminder);
			reminderVoMap.put(reminder.getPluginId(), reminderVo);
		}
	}
}
