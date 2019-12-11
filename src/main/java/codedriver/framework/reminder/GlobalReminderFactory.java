package codedriver.framework.reminder;


import codedriver.framework.common.RootComponent;
import codedriver.framework.reminder.dto.GlobalReminderVo;
import codedriver.framework.reminder.dao.mapper.GlobalReminderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RootComponent
public class GlobalReminderFactory implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private GlobalReminderMapper reminderMapper;

	private static Map<String, IGlobalReminder> reminderMap = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(GlobalReminderFactory.class);

	public static IGlobalReminder getReminder(String name) {
		if (!reminderMap.containsKey(name)) {
			throw new RuntimeException("找不到名称为：" + name +" 的实时动态插件");
		}
		return reminderMap.get(name);
	}

	@PostConstruct
	private void resetIsLoadOfAllSystemRemindPlugin() {
		reminderMapper.resetIsActiveOfAllReminder();
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IGlobalReminder> map = context.getBeansOfType(IGlobalReminder.class);
		for (Map.Entry<String, IGlobalReminder> entry : map.entrySet()) {
			IGlobalReminder reminder = entry.getValue();
			try {
				GlobalReminderVo reminderVo = new GlobalReminderVo();
				reminderVo.setName(reminder.getName());
				reminderVo.setReminderParamList(reminder.getConfig());
				reminderVo.setModuleId(context.getId());
				reminderVo.setDescription(reminder.getDescription());
				reminderVo.setPluginId(reminder.getPluginId());
				reminderVo.setIsActive(1);
				if (reminderMapper.getReminderCountByPluginId(reminder.getPluginId()) < 1) {
					reminderMapper.insertReminder(reminderVo);
				} else {
					reminderMapper.updateReminderByPluginId(reminderVo);
				}
				reminderMap.put(reminder.getPluginId(), reminder);
			} catch (Exception e) {
				logger.error("消息统计插件：" + reminder.getName() + "加载失败," + e.getMessage());
			}
		}
	}
}
