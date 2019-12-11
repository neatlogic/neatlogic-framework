package codedriver.framework.reminder.api;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.reminder.dto.GlobalReminderVo;
import codedriver.framework.reminder.service.GlobalReminderService;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-10 11:35
 **/
@Service
public class ReminderSearchApi extends ApiComponentBase {

    @Autowired
    private GlobalReminderService reminderService;

    @Override
    public String getToken() {
        return "globalReminder/search";
    }

    @Override
    public String getName() {
        return "实时动态插件检索接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({ @Param(name = "moduleId", type = ApiParamType.LONG, desc = "模块ID")})
    @Output({ @Param(name = "reminderList", type = ApiParamType.JSONARRAY, desc = "实时动态插件集合")})
    @Description(desc = "实时动态插件检索接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        GlobalReminderVo reminderVo = new GlobalReminderVo();
        reminderVo.setModuleId(jsonObj.getLong("moduleId"));
        JSONObject returnJson = new JSONObject();
        reminderVo.setUserId(UserContext.get().getUserId());
        List<GlobalReminderVo> reminderVoList = reminderService.searchReminder(reminderVo);
        Collections.sort(reminderVoList);
        returnJson.put("reminderList", reminderVoList);
        return returnJson;
    }
}
