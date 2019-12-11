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
 * @create: 2019-12-10 11:59
 **/
@Service
public class UserReminderCountApi extends ApiComponentBase {

    @Autowired
    private GlobalReminderService reminderService;

    @Override
    public String getToken() {
        return "globalReminder/getRealTimeMessageCount";
    }

    @Override
    public String getName() {
        return "获取实时动态消息数量接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Output({ @Param(name = "messageCount", type = ApiParamType.INTEGER, desc = "实时动态消息数量")})
    @Description(desc = "获取实时动态消息数量接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        JSONObject returnObj = new JSONObject();
        returnObj.put("messageCount", reminderService.getReminderMessageCount(UserContext.get().getUserId()));
        return returnObj;
    }
}
