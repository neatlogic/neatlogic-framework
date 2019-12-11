package codedriver.framework.reminder.api;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.reminder.service.GlobalReminderService;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-10 11:54
 **/
@Service
public class ReminderAllUpdateApi extends ApiComponentBase {

    @Autowired
    private GlobalReminderService reminderService;

    @Override
    public String getToken() {
        return "globalReminder/updateAllActive";
    }

    @Override
    public String getName() {
        return "重置所有消息有效性接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Output({ @Param(name = "Status", type = ApiParamType.STRING, desc = "执行状态"),
              @Param(name = "Message", type = ApiParamType.STRING, desc = "错误信息")})
    @Description(desc = "重置所有消息有效性接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        JSONObject returnObj = new JSONObject();
        try {
            String userId = UserContext.get().getUserId();
            reminderService.updateAllMessageActive(userId);
            returnObj.put("Status", "OK");
        } catch (Exception e) {
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", e.getMessage());
        }
        return returnObj;
    }
}
