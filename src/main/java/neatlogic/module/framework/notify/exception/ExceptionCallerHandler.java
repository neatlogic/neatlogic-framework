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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author lvzk
 * @since 2023/04/21 15:52
 **/
@Component
public class ExceptionCallerHandler extends ExceptionNotifyParamHandlerBase {
    static Logger logger = LoggerFactory.getLogger(ExceptionCallerHandler.class);
    @Override
    public String getValue() {
        return ExceptionNotifyParam.EXCEPTIONCALLER.getValue();
    }

    @Override
    public Object getMyText(NotifyVo notifyVo) {
        try {
            return notifyVo.getCallerMessageHandlerClass().newInstance().getCallerMessage(notifyVo);
        }catch (Exception ex){
            logger.error(ex.getMessage(),ex);
            return StringUtils.EMPTY;
        }
    }
}
