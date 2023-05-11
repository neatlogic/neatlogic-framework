/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */

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
