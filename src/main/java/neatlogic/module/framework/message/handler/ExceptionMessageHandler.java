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
