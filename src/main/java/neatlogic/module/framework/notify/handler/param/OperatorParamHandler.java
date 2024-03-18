/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.notify.handler.param;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.notify.constvalue.CommonNotifyParam;
import neatlogic.framework.notify.core.INotifyParamHandler;
import neatlogic.framework.notify.core.INotifyTriggerType;
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
    public Object getText(Object object, INotifyTriggerType notifyTriggerType) {
        UserContext userContext = UserContext.get();
        if (userContext != null) {
            return userContext.getUserName();
        }
        return null;
    }
}
