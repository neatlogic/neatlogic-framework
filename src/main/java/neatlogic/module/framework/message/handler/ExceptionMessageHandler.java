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

package neatlogic.module.framework.message.handler;

import neatlogic.framework.message.constvalue.PopUpType;
import neatlogic.framework.message.core.MessageHandlerBase;
import neatlogic.framework.notify.dto.NotifyVo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExceptionMessageHandler extends MessageHandlerBase {

    @Override
    public String getName() {
        return "通知异常消息处理器";
    }

    @Override
    public String getDescription() {
        return "异常消息发送时使用该处理器处理消息";
    }

    @Override
    public boolean getNeedCompression() {
        return false;
    }

    @Override
    public NotifyVo compress(List<NotifyVo> notifyVoList) {
        return null;
    }

    @Override
    public String getPopUp(){
        return PopUpType.LONGSHOW.getValue();
    }
}
