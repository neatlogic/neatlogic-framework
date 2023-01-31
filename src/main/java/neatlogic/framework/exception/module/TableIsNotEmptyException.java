/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.module;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class TableIsNotEmptyException extends ApiRuntimeException {
    public TableIsNotEmptyException(String name) {
        super("表“" + name + "”不为空，初始化失败");
    }
}
