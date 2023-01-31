/*
 * Copyright(c) 2021. TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ParamRepeatsException extends ApiRuntimeException {

    private static final long serialVersionUID = -7420026194484040260L;

    public ParamRepeatsException(String param) {
        super("参数“" + param + "”重复");
    }

    public ParamRepeatsException(int index, String keyName) {
        super("第：" + index + "个参数的'" + keyName + "'重复");
    }

}
