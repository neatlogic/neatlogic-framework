package codedriver.framework.reminder.api;

import codedriver.framework.apiparam.core.ApiParamType;
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
 * @create: 2019-12-10 11:36
 **/
@Service
public class ReminderModuleListApi extends ApiComponentBase {

    @Autowired
    private GlobalReminderService reminderService;

    @Override
    public String getToken() {
        return "globalReminder/reminderModuleList";
    }

    @Override
    public String getName() {
        return "查询实时动态模块列表接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Output({@Param(name = "reminderModuleList", type = ApiParamType.JSONARRAY, desc = "实时动态模块集合")})
    @Description(desc = "查询实时动态模块列表接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        JSONObject returnJson = new JSONObject();
        returnJson.put("reminderModuleList", reminderService.getActiveReminderModuleList());
        return returnJson;
    }
}
