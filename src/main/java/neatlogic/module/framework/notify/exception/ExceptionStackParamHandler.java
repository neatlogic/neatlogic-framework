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

import neatlogic.framework.notify.dto.NotifyVo;
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
        return notifyVo.getError();
    }
}
