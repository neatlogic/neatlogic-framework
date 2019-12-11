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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-10 12:08
 **/
@Service
public class CounterListInfoApi extends ApiComponentBase {

    @Autowired
    private GlobalCounterService counterService;

    @Override
    public String getToken() {
        return "globalCounter/getCounterInfo";
    }

    @Override
    public String getName() {
        return "查询消息统计信息集合接口";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Output({ @Param(name = "counterList", type = ApiParamType.JSONARRAY, desc = "数据集合"),
            @Param(name = "id", type = ApiParamType.LONG, desc="主键ID"),
            @Param(name = "name", type = ApiParamType.STRING, desc = "模块名称"),
            @Param(name = "moduleIcon", type = ApiParamType.STRING, desc = "图标"),
            @Param(name = "data", type = ApiParamType.JSONOBJECT, desc = "插件配置信息"),
            @Param(name = "showTemplate", type = ApiParamType.STRING, desc = "模板路径")})
    @Description(desc = "查询消息统计信息集合接口")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        JSONObject returnObj = new JSONObject();
        String userId = UserContext.get().getUserId();
        List<GlobalCounterVo> counterVoList = counterService.getSubscribeCounterListByUserId(userId);
        JSONArray dataArray = new JSONArray();
        if (counterVoList != null && counterVoList.size() > 0){
            for (GlobalCounterVo counter : counterVoList){
                JSONObject remindObj = new JSONObject();
                IGlobalCounter counterPlugin = GlobalCounterFactory.getCounter(counter.getName());
                Object data = counterPlugin.getShowData();
                remindObj.put("id", counter.getId());
                remindObj.put("name", counter.getModuleName());
                remindObj.put("moduleIcon", counter.getModuleIcon());
                remindObj.put("data", data);
                remindObj.put("showTemplate", counterPlugin.getShowTemplate());
                dataArray.add(remindObj);
            }
        }
        returnObj.put("counterList", dataArray);
        return returnObj;
    }
}
