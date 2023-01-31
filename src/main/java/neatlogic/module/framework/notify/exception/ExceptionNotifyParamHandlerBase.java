/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.notify.exception;

import neatlogic.framework.notify.core.INotifyParamHandler;
import neatlogic.framework.notify.dto.NotifyVo;

/**
 * @author laiwt
 * @since 2021/10/28 16:55
 **/
public abstract class ExceptionNotifyParamHandlerBase implements INotifyParamHandler {

    @Override
    public Object getText(Object object) {
        if (object instanceof NotifyVo) {
            return getMyText((NotifyVo) object);
        }
        return null;
    }

    public abstract Object getMyText(NotifyVo notifyVo);
}
