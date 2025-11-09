/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.module.framework.notify.exception;

import neatlogic.framework.notify.core.INotifyParamHandler;
import neatlogic.framework.notify.core.INotifyTriggerType;
import neatlogic.framework.notify.dto.NotifyVo;

/**
 * @author laiwt
 * @since 2021/10/28 16:55
 **/
public abstract class ExceptionNotifyParamHandlerBase implements INotifyParamHandler {

    @Override
    public Object getText(Object object, INotifyTriggerType notifyTriggerType) {
        if (object instanceof NotifyVo) {
            return getMyText((NotifyVo) object);
        }
        return null;
    }

    public abstract Object getMyText(NotifyVo notifyVo);
}
