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

    @Input({ @Param(name = "sortPluginIdStr", type = ApiParamType.STRING, desc = "排序后的ID拼接字符串")})
    @Description(desc = "统计消息重排序接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        String sortIdStr = jsonObj.getString("sortPluginIdStr");
        String userId = UserContext.get().getUserId();
        counterService.updateCounterUserSort(userId, sortIdStr);
        return null;
    }
}
