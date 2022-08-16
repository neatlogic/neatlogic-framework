/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.lcs.exception;

import codedriver.framework.exception.core.ApiRuntimeException;

/**
 * @author linbq
 * @since 2021/8/10 10:30
 **/
public class LineHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 1669102356313895841L;

    public LineHandlerNotFoundException(String handler) {
        super("知识插件：'" + handler + "'不存在");
    }
}
