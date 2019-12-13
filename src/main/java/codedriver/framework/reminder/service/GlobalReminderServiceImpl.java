package codedriver.framework.reminder.service;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.reminder.core.GlobalReminderFactory;
import codedriver.framework.reminder.dto.GlobalReminderMessageVo;
import codedriver.framework.reminder.dto.GlobalReminderSubscribeVo;
import codedriver.framework.reminder.dto.GlobalReminderVo;
import codedriver.framework.reminder.dto.ReminderMessageSearchVo;
import codedriver.framework.reminder.dao.mapper.GlobalReminderMapper;
import codedriver.framework.reminder.dao.mapper.GlobalReminderMessageMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: balantflow
 * @description:
 * @create: 2019-09-10 20:03
 **/
@Service
@Transactional
public class GlobalReminderServiceImpl implements GlobalReminderService {

    @Autowired
    private GlobalReminderMapper reminderMapper;

    @Autowired
    private GlobalReminderMessageMapper reminderMessageMapper;

    @Override
    public void updateReminderSubscribe(GlobalReminderSubscribeVo reminderSubscribeVo) {
        int isActive = reminderSubscribeVo.getIsActive();
        if (reminderSubscribeVo.getId() != null){
                if (isActive == 1){
                reminderMapper.updateReminderSubscribe(reminderSubscribeVo);
            }else {
                reminderMapper.deleteReminderSubscribe(reminderSubscribeVo);
                //取消订阅，移除该控件所有消息的有效性
                reminderMessageMapper.updateMessageActiveByReminderId(reminderSubscribeVo.getUserId(), reminderSubscribeVo.getPluginId());
            }
        }else {
            reminderMapper.insertReminderSubscribe(reminderSubscribeVo);
        }
    }

    @Override
    public List<GlobalReminderVo> searchReminder(GlobalReminderVo reminderVo) {
        boolean moduleId = StringUtils.isNotBlank(reminderVo.getModuleId());
        List<GlobalReminderVo> activeReminderList = new ArrayList<>();
        List<GlobalReminderVo> reminderVoList = GlobalReminderFactory.getReminderVoList();
        Map<String, ModuleVo> moduleVoMap = TenantContext.get().getActiveModuleMap();
        for (GlobalReminderVo c : reminderVoList) {
            if (moduleVoMap.containsKey(c.getModuleId())) {
                c.setModuleName(moduleVoMap.get(c.getModuleId()).getName());
                c.setModuleName(moduleVoMap.get(c.getModuleId()).getDescription());
                if (!moduleId || (moduleId && c.getModuleId().equals(reminderVo.getModuleId()))) {
                    activeReminderList.add(c);
                }
            }
        }
            List<GlobalReminderSubscribeVo> reminderSubList = reminderMapper.getReminderSubscribeListByUserId(UserContext.get().getUserId());
            Map<String, GlobalReminderSubscribeVo> subscribeMap = new HashMap<>();
            for (GlobalReminderSubscribeVo subscribeVo : reminderSubList) {
                subscribeMap.put(subscribeVo.getPluginId(), subscribeVo);
            }


            for (GlobalReminderVo reminder : activeReminderList) {
                if (subscribeMap.containsKey(reminder.getPluginId())) {
                    reminder.setReminderSubscribeVo(subscribeMap.get(reminder.getPluginId()));
                }
            }
        return activeReminderList;
    }

    @Override
    public List<ModuleVo> getActiveReminderModuleList () {
        List<ModuleVo> moduleList = new ArrayList<>();
        List<ModuleVo> tenantModuleList = TenantContext.get().getActiveModuleList();
        List<GlobalReminderVo> reminderList = GlobalReminderFactory.getReminderVoList();
        for (ModuleVo moduleVo : tenantModuleList) {
            for (GlobalReminderVo reminderVo : reminderList) {
                if (moduleVo.getId().equals(reminderVo.getModuleId())) {
                    moduleList.add(moduleVo);
                    break;
                }
            }
        }
        return moduleList;
    }

    @Override
    public List<GlobalReminderMessageVo> getDayReminderMessageVoListByUserId(String userId, Long messageId, Integer day) {
        Map<String, String> timeMap = getTimeMap(day);
        ReminderMessageSearchVo searchVo = new ReminderMessageSearchVo();
        if (messageId != null && messageId != 0L){
            searchVo.setMessageCount(ReminderMessageSearchVo.DEFAULT_ADD_COUNT);
            searchVo.setMessageId(messageId);
        }else {
            searchVo.setMessageCount(ReminderMessageSearchVo.DEFAULT_SHOW_COUNT);
        }
        searchVo.setStartTime(timeMap.get("startTime"));
        searchVo.setEndTime(timeMap.get("endTime"));
        searchVo.setUserId(userId);
        List<GlobalReminderMessageVo> messageVoList = reminderMessageMapper.getShowReminderMessageListByIdListAndUserId(searchVo);
        for (GlobalReminderMessageVo messageVo : messageVoList){
            packageData(messageVo);
        }
        return messageVoList;
    }

    @Override
    public int getReminderMessageCountByDay(int day, String userId) {
        Map<String, String> timeMap = getTimeMap(day);
        ReminderMessageSearchVo searchVo = new ReminderMessageSearchVo();
        searchVo.setUserId(userId);
        searchVo.setStartTime(timeMap.get("startTime"));
        searchVo.setEndTime(timeMap.get("endTime"));
        return reminderMessageMapper.getReminderMessageCountByDay(searchVo);
    }

    @Override
    public List<GlobalReminderMessageVo> getScheduleMessageList(String userId) {
        List<GlobalReminderMessageVo> messageVoList = reminderMessageMapper.getScheduleMessageList(userId);
        for (GlobalReminderMessageVo messageVo : messageVoList){
            packageData(messageVo);
        }
        return messageVoList;
    }

    @Override
    public void updateMessageActive(Long messageId, String userId) {
        reminderMessageMapper.updateMessageActiveById(messageId, userId);
    }

    @Override
    public void updateAllMessageActive(String userId) {
        reminderMessageMapper.updateAllMessageActive(userId);
    }

    @Override
    public void updateDayMessageActive(String userId, int day) {
        Map<String, String> timeMap = getTimeMap(day);
        ReminderMessageSearchVo searchVo = new ReminderMessageSearchVo();
        searchVo.setUserId(userId);
        searchVo.setStartTime(timeMap.get("startTime"));
        searchVo.setEndTime(timeMap.get("endTime"));
        reminderMessageMapper.updateDayMessageActive(searchVo);
    }

    @Override
    public int getReminderMessageCount(String userId) {
        return reminderMessageMapper.getReminderMessageCount(userId);
    }

    @Override
    public void updateMessageKeepStatus(String userId, Long messageId) {
        reminderMessageMapper.updateMessageKeepStatus(userId, messageId);
    }

    public Map<String, String> getTimeMap(int day){
        Map<String, String> timeMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + 1);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - day);
        String endTime = format.format(calendar.getTime());
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - 1);
        String startTime = format.format(calendar.getTime());
        timeMap.put("startTime", startTime);
        timeMap.put("endTime", endTime);
        return timeMap;
    }

    public void packageData(GlobalReminderMessageVo messageVo){
        GlobalReminderVo reminderVo = GlobalReminderFactory.getReminderVoMap().get(messageVo.getPluginId());
        ModuleVo moduleVo = TenantContext.get().getActiveModuleMap().get(reminderVo.getModuleId());
        reminderVo.setModuleDesc(moduleVo.getDescription());
        reminderVo.setModuleName(moduleVo.getName());
        messageVo.setReminderVo(reminderVo);
    }
}
