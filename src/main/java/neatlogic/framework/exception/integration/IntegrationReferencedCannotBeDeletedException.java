/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.exception.integration;

import neatlogic.framework.exception.core.ApiRuntimeException;

/**
 * @author: linbq
 * @since: 2021/4/12 14:12
 **/
public class IntegrationReferencedCannotBeDeletedException extends ApiRuntimeException {

    private static final long serialVersionUID = 3459303360397256808L;

    public IntegrationReferencedCannotBeDeletedException(String uuid) {
        super("集成：'" + uuid + "'有被引用，不能删除");
    }
}
