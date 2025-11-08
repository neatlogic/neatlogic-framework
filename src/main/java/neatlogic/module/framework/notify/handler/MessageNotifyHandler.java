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

package neatlogic.module.framework.notify.handler;

import neatlogic.framework.message.core.IMessageHandler;
import neatlogic.framework.message.core.MessageHandlerFactory;
import neatlogic.framework.notify.core.NotifyHandlerBase;
import neatlogic.framework.notify.core.NotifyHandlerType;
import neatlogic.framework.notify.dto.NotifyVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
public class MessageNotifyHandler extends NotifyHandlerBase {

    @Override
    protected boolean myExecute(NotifyVo notifyVo) {
        if (CollectionUtils.isNotEmpty(notifyVo.getToUserUuidList()) || CollectionUtils.isNotEmpty(notifyVo.getToTeamUuidList()) || CollectionUtils.isNotEmpty(notifyVo.getToRoleUuidList())) {
            IMessageHandler handler = MessageHandlerFactory.getHandler(notifyVo.getMessageHandlerClass().getName());
            handler.send(notifyVo);
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return NotifyHandlerType.MESSAGE.getText();
    }

    @Override
    public String getType() {
        return NotifyHandlerType.MESSAGE.getValue();
    }
}
