package codedriver.framework.reminder.api;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.reminder.dto.GlobalReminderMessageVo;
import codedriver.framework.reminder.GlobalReminderFactory;
import codedriver.framework.reminder.IGlobalReminder;
import codedriver.framework.reminder.service.GlobalReminderService;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-10 11:39
 **/
@Service
public class DayReminderApi extends ApiComponentBase {

    @Autowired
    private GlobalReminderService reminderService;

    @Override
    public String getToken() {
        return "globalReminder/dayMessage";
    }

    @Override
    public String getName() {
        return "查询当天消息列表接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({ @Param(name = "messageId", type = ApiParamType.LONG, desc = "消息ID"),
             @Param(name = "day", type = ApiParamType.INTEGER, desc = "天数"),
             @Param(name = "messageCount", type = ApiParamType.INTEGER, desc = "消息数量")})
    @Output({ @Param(name = "messageArray", type = ApiParamType.JSONARRAY, desc = "消息集合"),
              @Param(name = "messageCount", type = ApiParamType.INTEGER, desc = "消息数量"),
              @Param(name = "lastMessageId", type = ApiParamType.LONG, desc = "最新消息ID"),
              @Param(name = "showDay", type = ApiParamType.STRING, desc = "时间展示"),
              @Param(name = "day", type = ApiParamType.INTEGER, desc = "天数"),
              @Param(name = "allMessageCount", type = ApiParamType.INTEGER, desc = "消息总数")})
    @Description(desc = "查询当天消息列表接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        long messageId = jsonObj.getLong("messageId");
        int day = jsonObj.getInteger("day");
        int messageCount = jsonObj.getInteger("messageCount");
        JSONObject returnJson = new JSONObject();
        String userId = UserContext.get().getUserId();
        List<GlobalReminderMessageVo> messageList = reminderService.getDayReminderMessageVoListByUserId(userId, messageId, day);
        JSONArray messageArray = new JSONArray();
        for (GlobalReminderMessageVo messageVo : messageList){
            IGlobalReminder reminder = GlobalReminderFactory.getReminder(messageVo.getReminderVo().getName());
            messageArray.add(reminder.packData(messageVo));
        }
        Long lastMessageId = 0L;
        if (messageList != null && messageList.size() > 0){
            lastMessageId = messageList.get(messageList.size() - 1).getId();
        }
        returnJson.put("messageArray", messageArray);
        returnJson.put("messageCount", messageList.size() + messageCount);
        returnJson.put("lastMessageId", lastMessageId);
        returnJson.put("showDay", getShowDay(day));
        returnJson.put("day", day);
        returnJson.put("allMessageCount", reminderService.getReminderMessageCountByDay(day, userId));
        return returnJson;
    }

    public String getShowDay(Integer day){
        if (day == 0){
            return "今天";
        }
        return day + "天前";
    }
}
