/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.notify.exception;

import codedriver.framework.notify.core.INotifyParamHandler;
import codedriver.framework.notify.dto.NotifyVo;

/**
 * @author laiwt
 * @since 2021/10/28 16:55
 **/
public abstract class ExceptionNotifyParamHandlerBase implements INotifyParamHandler {

    @Override
    public Object getText(Object object) {
        return getMyText((NotifyVo) object);
    }

    public abstract Object getMyText(NotifyVo notifyVo);
}
