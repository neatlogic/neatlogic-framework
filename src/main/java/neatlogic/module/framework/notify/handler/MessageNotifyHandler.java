/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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
