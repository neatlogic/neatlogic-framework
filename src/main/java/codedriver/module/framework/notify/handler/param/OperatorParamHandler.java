/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.notify.handler.param;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.notify.constvalue.CommonNotifyParam;
import codedriver.framework.notify.core.INotifyParamHandler;
import org.springframework.stereotype.Component;

/**
 * @author linbq
 * @since 2021/10/16 15:52
 **/
@Component
public class OperatorParamHandler implements INotifyParamHandler {

    @Override
    public String getValue() {
        return CommonNotifyParam.OPERATOR.getValue();
    }

    @Override
    public Object getText(Object object) {
        UserContext userContext = UserContext.get();
        if (userContext != null) {
            return userContext.getUserName();
        }
        return null;
    }
}
