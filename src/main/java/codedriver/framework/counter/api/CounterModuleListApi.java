package codedriver.framework.counter.api;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.counter.service.GlobalCounterService;
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
 * @create: 2019-12-10 12:06
 **/
@Service
public class CounterModuleListApi extends ApiComponentBase {

    @Autowired
    private GlobalCounterService counterService;

    @Override
    public String getToken() {
        return "globalCounter/counterModuleList";
    }

    @Override
    public String getName() {
        return "查询消息统计模块列表接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Output({@Param(name = "counterModuleList", type = ApiParamType.JSONARRAY, desc = "模块列表")})
    @Description(desc = "查询消息统计模块列表接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        JSONObject returnJson = new JSONObject();
        returnJson.put("counterModuleList", counterService.getActiveCounterModuleList());
        return returnJson;
    }
}
