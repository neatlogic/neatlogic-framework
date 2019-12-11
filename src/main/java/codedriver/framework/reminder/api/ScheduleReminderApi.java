package codedriver.framework.reminder.api;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.reminder.dto.GlobalReminderMessageVo;
import codedriver.framework.reminder.GlobalReminderFactory;
import codedriver.framework.reminder.IGlobalReminder;
import codedriver.framework.reminder.service.GlobalReminderService;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.core.ApiComponentBase;
import codedriver.framework.restful.annotation.Param;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-10 11:41
 **/
@Service
public class ScheduleReminderApi extends ApiComponentBase {

    @Autowired
    private GlobalReminderService reminderService;

    @Override
    public String getToken() {
        return "globalReminder/scheduleDayMessage";
    }

    @Override
    public String getName() {
        return "定时获取新消息接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Output({ @Param(name = "scheduleMessageList", type = ApiParamType.JSONARRAY, desc = "消息集合")})
    @Description(desc = "定时获取新消息接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        JSONObject returnObj = new JSONObject();
        List<GlobalReminderMessageVo> messageVos = reminderService.getScheduleMessageList(UserContext.get().getUserId());
        Collections.sort(messageVos);
        JSONArray messageArray = new JSONArray();
        for (GlobalReminderMessageVo messageVo : messageVos){
            IGlobalReminder reminder = GlobalReminderFactory.getReminder(messageVo.getReminderVo().getName());
            messageArray.add(reminder.packData(messageVo));
        }
        returnObj.put("scheduleMessageList", messageArray);
        return returnObj;
    }
}
