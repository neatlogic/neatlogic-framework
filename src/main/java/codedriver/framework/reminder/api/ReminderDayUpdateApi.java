package codedriver.framework.reminder.api;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.reminder.service.GlobalReminderService;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-10 11:55
 **/
@Service
public class ReminderDayUpdateApi extends ApiComponentBase {

    @Autowired
    private GlobalReminderService reminderService;

    @Override
    public String getToken() {
        return "globalReminder/updateDayActive";
    }

    @Override
    public String getName() {
        return "重置当天消息有效性接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({ @Param(name = "day", type = ApiParamType.INTEGER, desc = "天数")})
    @Output({ @Param(name = "Status", type = ApiParamType.STRING, desc = "执行状态"),
            @Param(name = "Message", type = ApiParamType.STRING, desc = "错误信息")})
    @Description(desc = "重置当天消息有效性接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        int day = jsonObj.getInteger("day");
        JSONObject returnObj = new JSONObject();
        try {
            String userId = UserContext.get().getUserId();
            reminderService.updateDayMessageActive(userId, day);
            returnObj.put("Status", "OK");
        } catch (Exception e) {
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", e.getMessage());
        }
        return returnObj;
    }
}
