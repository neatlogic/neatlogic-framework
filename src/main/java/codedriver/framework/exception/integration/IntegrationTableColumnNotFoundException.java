/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.exception.integration;

import codedriver.framework.exception.core.ApiRuntimeException;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class IntegrationTableColumnNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1061691151919475176L;

    public IntegrationTableColumnNotFoundException(String integration, String columnUuid) {
        super("在" + integration + "集成配置输出转换theadList列表中找不到：'" + columnUuid + "'列信息");
    }

}
