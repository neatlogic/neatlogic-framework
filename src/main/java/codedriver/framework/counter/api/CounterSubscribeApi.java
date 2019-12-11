package codedriver.framework.counter.api;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.counter.dto.GlobalCounterSubscribeVo;
import codedriver.framework.counter.service.GlobalCounterService;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.core.ApiComponentBase;
import codedriver.framework.restful.annotation.Param;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-10 12:10
 **/
@Service
public class CounterSubscribeApi extends ApiComponentBase {

    @Autowired
    private GlobalCounterService counterService;

    @Override
    public String getToken() {
        return "globalCounter/subscribe";
    }

    @Override
    public String getName() {
        return "消息统计模块订阅接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({ @Param(name = "id", type = ApiParamType.LONG, desc = "主键ID"),
             @Param(name = "counterId", type = ApiParamType.LONG, desc = "消息统计插件ID")})
    @Output({ @Param(name = "id", type = ApiParamType.LONG, desc = "主键ID"),
              @Param(name = "Status", type = ApiParamType.STRING, desc = "执行状态"),
              @Param(name = "Message", type = ApiParamType.STRING, desc = "错误信息")})
    @Description(desc = "消息统计模块订阅接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        String userId = UserContext.get().getUserId();
        Long id = jsonObj.getLong("id");
        Long counterId = jsonObj.getLong("counterId");
        GlobalCounterSubscribeVo counterSubscribeVo = new GlobalCounterSubscribeVo();
        counterSubscribeVo.setId(id);
        counterSubscribeVo.setCounterId(counterId);
        counterSubscribeVo.setUserId(userId);
        JSONObject returnJson = new JSONObject();
        try {
            counterService.updateCounterSubscribe(counterSubscribeVo);
            returnJson.put("id", counterSubscribeVo.getId());
            returnJson.put("Status", "OK");
        } catch (Exception e) {
            returnJson.put("Status", "ERROR");
            returnJson.put("Message", e.getMessage());
        }
        return returnJson;
    }
}
