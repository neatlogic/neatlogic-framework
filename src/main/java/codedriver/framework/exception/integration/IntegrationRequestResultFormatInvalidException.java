/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.integration;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @author linbq
 * @since 2021/11/23 12:12
 **/
public class IntegrationRequestResultFormatInvalidException extends ApiRuntimeException {

    private static final long serialVersionUID = 1161601312349475176L;

    public IntegrationRequestResultFormatInvalidException() {
        super("集成请求结果不符合JSON格式");
    }
}
