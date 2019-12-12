package codedriver.framework.inform.core;

import codedriver.framework.apiparam.core.ApiParamType;
import codedriver.framework.inform.dto.EmailInformVo;
import codedriver.framework.inform.dto.InformBaseVo;
import codedriver.framework.restful.annotation.Description;
import codedriver.framework.restful.annotation.Input;
import codedriver.framework.restful.annotation.Output;
import codedriver.framework.restful.core.ApiComponentBase;
import codedriver.framework.restful.annotation.Param;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-11 15:20
 **/
@Service
public class TestApi extends ApiComponentBase {

    @Override
    public String getToken() {
        return "inform/emailtest";
    }

    @Override
    public String getName() {
        return "emailTest";
    }

    @Override
    public String getConfig() {
        return null;
    }

    @Input({ @Param( name = "user", type = ApiParamType.STRING, isRequired = true, desc = "userId")})
    @Output( {@Param( name = "Status", type = ApiParamType.STRING, desc = "状态")})
    @Description(desc = "emailTest")
    @Override
    public Object myDoService(JSONObject jsonObj) throws Exception {
        InformBaseVo informVo = new EmailInformVo();
        informVo.setTitle("test title");
        informVo.setContent("test content");
        informVo.setFromUser("jim");
        informVo.setPluginId(InformPluginType.EMAIL.getValue());
        List<String> list = new ArrayList<>();
        list.add(jsonObj.getString("user"));
        informVo.setToUserIdList(list);

        ((EmailInformVo) informVo).setCcUserIdList(list);
        InformComponentManager.inform(informVo);
        JSONObject obj = new JSONObject();
        obj.put("Status", "OK");
        return obj;
    }
}
