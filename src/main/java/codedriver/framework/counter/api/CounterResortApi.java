package codedriver.framework.counter.api;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.counter.service.GlobalCounterService;
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
 * @create: 2019-12-10 12:12
 **/
@Service
public class CounterResortApi extends ApiComponentBase {

    @Autowired
    private GlobalCounterService counterService;

    @Override
    public String getToken() {
        return "globalCounter/counterReSort";
    }

    @Override
    public String getName() {
        return "消息统计重排序接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({ @Param(name = "sortIdStr", type = ApiParamType.STRING, desc = "排序后的ID拼接字符串")})
    @Output({ @Param(name = "Status", type = ApiParamType.STRING, desc = "接口执行状态"),
            @Param(name = "Message", type = ApiParamType.STRING, desc = "错误信息")})
    @Description(desc = "统计消息重排序接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        String sortIdStr = jsonObj.getString("sortIdStr");
        String userId = UserContext.get().getUserId();
        JSONObject returnJson = new JSONObject();
        try {
            counterService.updateCounterUserSort(userId, sortIdStr);
            returnJson.put("Status", "OK");
        } catch (Exception e) {
            returnJson.put("Status", "ERROR");
            returnJson.put("Message", e.getMessage());
        }
        return returnJson;
    }
}
