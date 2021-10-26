/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.integration;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @author linbq
 * @since 2021/10/22 18:25
 **/
public class IntegrationTablePrimaryKeyColumnNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1061691151919475176L;

    public IntegrationTablePrimaryKeyColumnNotFoundException(String integration) {
        super("在" + integration + "集成配置输出转换theadList列表中找不到主键列");
    }
}
