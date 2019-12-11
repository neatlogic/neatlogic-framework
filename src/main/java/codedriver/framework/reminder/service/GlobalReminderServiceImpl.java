package codedriver.framework.reminder.service;

import codedriver.framework.dto.ModuleVo;
import codedriver.framework.reminder.dto.GlobalReminderMessageVo;
import codedriver.framework.reminder.dto.GlobalReminderSubscribeVo;
import codedriver.framework.reminder.dto.GlobalReminderVo;
import codedriver.framework.reminder.dto.ReminderMessageSearchVo;
import codedriver.framework.reminder.dao.mapper.GlobalReminderMapper;
import codedriver.framework.reminder.dao.mapper.GlobalReminderMessageMapper;
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
                reminderMessageMapper.updateMessageActiveByReminderId(reminderSubscribeVo.getUserId(), reminderSubscribeVo.getReminderId());
            }
        }else {
            reminderMapper.insertReminderSubscribe(reminderSubscribeVo);
        }
    }

    @Override
    public List<GlobalReminderVo> searchReminder(GlobalReminderVo reminderVo) {
        return reminderMapper.getReminderList(reminderVo);
    }

    @Override
    public List<ModuleVo> getActiveReminderModuleList() {
        return reminderMapper.getActiveReminderModuleList();
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
        Map<String, ModuleVo> tenantModuleMap = new HashMap<>();
        List<GlobalReminderMessageVo> messageVoList = reminderMessageMapper.getShowReminderMessageListByIdListAndUserId(searchVo);
        for (GlobalReminderMessageVo messageVo : messageVoList){
            GlobalReminderVo reminderVo = messageVo.getReminderVo();
            reminderVo.setModuleName(tenantModuleMap.get(reminderVo.getModuleId()).getName());
            reminderVo.setDescription(tenantModuleMap.get(reminderVo.getModuleId()).getDescription());
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
        Map<String, ModuleVo> tenantModuleMap = new HashMap<>();
        List<GlobalReminderMessageVo> messageVoList = reminderMessageMapper.getScheduleMessageList(userId);
        for (GlobalReminderMessageVo messageVo : messageVoList){
            GlobalReminderVo reminderVo = messageVo.getReminderVo();
            reminderVo.setModuleName(tenantModuleMap.get(reminderVo.getModuleId()).getName());
            reminderVo.setDescription(tenantModuleMap.get(reminderVo.getModuleId()).getDescription());
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
}
