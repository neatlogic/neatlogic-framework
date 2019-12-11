package codedriver.framework.counter.api;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.counter.dto.GlobalCounterVo;
import codedriver.framework.counter.GlobalCounterFactory;
import codedriver.framework.counter.IGlobalCounter;
import codedriver.framework.counter.service.GlobalCounterService;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.annotation.Param;
import codedriver.framework.restful.core.ApiComponentBase;
import codedriver.framework.restful.annotation.Input;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-10 12:05
 **/
@Service
public class CounterSearchApi extends ApiComponentBase {

    @Autowired
    private GlobalCounterService counterService;

    @Override
    public String getToken() {
        return "globalCounter/search";
    }

    @Override
    public String getName() {
        return "消息统计插件检索接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({ @Param(name = "moduleId", type = ApiParamType.LONG, desc = "模块ID")})
    @Output({ @Param(name = "counterList", type = ApiParamType.JSONARRAY, desc = "消息统计插件列表")})
    @Description(desc = "消息统计插件检索接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        GlobalCounterVo counter = new GlobalCounterVo();
        JSONObject returnJson = new JSONObject();
        String userId = UserContext.get().getUserId();
        String moduleId = jsonObj.getString("moduleId");
        counter.setModuleId(moduleId);
        counter.setUserId(userId);
        List<GlobalCounterVo> counterList = counterService.searchCounterVo(counter);
        if (counterList != null && counterList.size() > 0){
            for (int i = 0; i < counterList.size(); i++){
                GlobalCounterVo counterVo = counterList.get(i);
                IGlobalCounter globalCounter = GlobalCounterFactory.getCounter(counterVo.getName());
                counterVo.setPreviewPath(globalCounter.getPreviewPath());
                counterVo.setShowTemplate(globalCounter.getShowTemplate());
            }
        }
        Collections.sort(counterList);
        returnJson.put("counterList", counterList);
        return returnJson;
    }
}
