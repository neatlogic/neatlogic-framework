package codedriver.framework.reminder.api;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.reminder.dto.GlobalReminderSubscribeVo;
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
 * @create: 2019-12-10 11:30
 **/
@Service
public class ReminderSubscribeApi extends ApiComponentBase {

    @Autowired
    private GlobalReminderService reminderService;

    @Override
    public String getToken() {
        return "globalReminder/subscribe";
    }

    @Override
    public String getName() {
        return "实时动态订阅接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({ @Param(name = "id", type = ApiParamType.LONG, desc = "数据主键ID"),
             @Param(name = "reminderId", type = ApiParamType.LONG, desc = "实时动态插件ID"),
             @Param(name = "param", type = ApiParamType.JSONOBJECT, desc = "插件配置参数"),
             @Param(name = "isActive", type = ApiParamType.INTEGER, desc = "插件状态")})
    @Output({ @Param(name = "Status", type = ApiParamType.STRING, desc = "执行状态"),
            @Param(name = "Message", type = ApiParamType.STRING, desc = "错误信息")})
    @Description(desc = "实时动态订阅接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        GlobalReminderSubscribeVo reminderSubscribe = new GlobalReminderSubscribeVo();
        Long id = jsonObj.getLong("id");
        Long reminderId = jsonObj.getLong("reminderId");
        JSONObject paramObj = jsonObj.getJSONObject("param");
        int isActive = jsonObj.getInteger("isActive");
        reminderSubscribe.setUserId(UserContext.get().getUserId());
        reminderSubscribe.setReminderId(reminderId);
        reminderSubscribe.setId(id);
        reminderSubscribe.setParam(paramObj.toJSONString());
        reminderSubscribe.setIsActive(isActive);
        JSONObject returnJson = new JSONObject();
        try {
            reminderService.updateReminderSubscribe(reminderSubscribe);
            returnJson.put("id", reminderSubscribe.getId());
            returnJson.put("Status", "OK");
        } catch (Exception e) {
            returnJson.put("Status", "ERROR");
            returnJson.put("Message", e.getMessage());
        }
        return returnJson;
    }
}
