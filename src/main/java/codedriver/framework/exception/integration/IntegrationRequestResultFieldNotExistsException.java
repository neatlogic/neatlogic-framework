/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.integration;

import codedriver.framework.exception.core.ApiRuntimeException;

import java.util.List;

/**
 * @author linbq
 * @since 2021/11/23 12:12
 **/
public class IntegrationRequestResultFieldNotExistsException extends ApiRuntimeException {

    private static final long serialVersionUID = 1161502312345475176L;

    public IntegrationRequestResultFieldNotExistsException(String field) {
        super("集成请求结果中：'" + field + "'字段不存在");
    }
    public IntegrationRequestResultFieldNotExistsException(List<String> fieldList) {
        super("集成请求结果中：'" + String.join("、", fieldList) + "'字段不存在");
    }
}
