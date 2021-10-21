package codedriver.framework.apiparam.validator;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.param.validate.core.ApiParamValidatorBase;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class LongApiParam extends ApiParamValidatorBase {

    @Override
    public String getName() {
        return "长整数";
    }

    @Override
    public boolean validate(Object param, String rule) {
        try {
            if (StringUtils.isBlank(param.toString())) {
                return true;
            }
            Long.valueOf(param.toString());
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public ApiParamType getType() {
        return ApiParamType.LONG;
    }


    @Override
    public void correctValue(JSONObject paramObj, String name, Object param) {
        if (StringUtils.isBlank(param.toString())) {
            paramObj.remove(name);
        }
    }
}
