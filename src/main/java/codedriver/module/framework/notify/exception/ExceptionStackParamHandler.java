/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.notify.exception;

import codedriver.framework.notify.dto.NotifyVo;
import org.springframework.stereotype.Component;

/**
 * @author laiwt
 * @since 2021/10/28 15:52
 **/
@Component
public class ExceptionStackParamHandler extends ExceptionNotifyParamHandlerBase {

    @Override
    public String getValue() {
        return ExceptionNotifyParam.EXCEPTIONSTACK.getValue();
    }

    @Override
    public Object getMyText(NotifyVo notifyVo) {
        return notifyVo.getException();
    }
}
