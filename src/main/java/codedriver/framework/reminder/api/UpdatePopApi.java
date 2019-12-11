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
 * @create: 2019-12-10 12:01
 **/
@Service
public class UpdatePopApi extends ApiComponentBase {

    @Autowired
    private GlobalReminderService reminderService;

    @Override
    public String getToken() {
        return "globalReminder/updateMessageKeep";
    }

    @Override
    public String getName() {
        return "重置弹窗有效性接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({ @Param( name = "idStr", type = ApiParamType.STRING, desc = "ID拼接字符串，多个使用“,” 隔开")})
    @Output({ @Param(name = "Status", type = ApiParamType.STRING, desc = "执行状态"),
            @Param(name = "Message", type = ApiParamType.STRING, desc = "错误信息")})
    @Description(desc = "重置弹窗有效性接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        String idStr = jsonObj.getString("idStr");
        JSONObject returnObj = new JSONObject();
        try {
            if (idStr != null && !("").equals(idStr)){
                String[] idArray = idStr.split(",");
                for (String id : idArray){
                    reminderService.updateMessageKeepStatus(UserContext.get().getUserId(), Long.parseLong(id));
                }
            }
            returnObj.put("Status", "OK");
        } catch (NumberFormatException e) {
            returnObj.put("Status", "ERROR");
            returnObj.put("Message", e.getMessage());
        }
        return returnObj;
    }

}
