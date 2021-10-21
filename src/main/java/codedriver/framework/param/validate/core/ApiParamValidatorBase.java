package codedriver.framework.param.validate.core;

import codedriver.framework.common.constvalue.ApiParamType;
import com.alibaba.fastjson.JSONObject;

public abstract class ApiParamValidatorBase {
    public abstract String getName();

    public abstract boolean validate(Object param, String rule);

    public abstract ApiParamType getType();

    /**
     * 纠正数据
     *
     * @param paramObj 入参json
     * @param name     key
     * @param param    值
     */
    public void correctValue(JSONObject paramObj, String name, Object param) {

    }
}
