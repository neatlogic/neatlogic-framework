/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.condition.handler;

import codedriver.framework.datawarehouse.condition.IDatasourceConditionHandler;

public class TextConditionHandler implements IDatasourceConditionHandler {
    @Override
    public String getExpression(Long fieldId, Object value) {
        if (value != null) {
            return "`" + fieldId + "` like '%" + value.toString() + "%'";
        }
        return null;
    }

    public String getName() {
        return "text";
    }
}
