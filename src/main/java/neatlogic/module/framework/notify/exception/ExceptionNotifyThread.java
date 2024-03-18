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

package neatlogic.module.framework.notify.exception;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.notify.core.INotifyTriggerType;
import neatlogic.framework.notify.dao.mapper.NotifyMapper;
import neatlogic.framework.notify.dto.NotifyPolicyConfigVo;
import neatlogic.framework.notify.dto.NotifyPolicyVo;
import neatlogic.framework.notify.dto.NotifyVo;
import neatlogic.framework.util.NotifyPolicyUtil;
import neatlogic.module.framework.message.handler.ExceptionMessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExceptionNotifyThread extends NeatLogicThread {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionNotifyThread.class);
    private static NotifyMapper notifyMapper;

    @Autowired
    public void setNotifyMapper(NotifyMapper _notifyMapper) {
        notifyMapper = _notifyMapper;
    }

    private NotifyVo notifyVo;
    private Exception exception;
    private INotifyTriggerType notifyTriggerType;

    public ExceptionNotifyThread() {
        super("EXCEPTION-NOTIFY");
    }

    public ExceptionNotifyThread(NotifyVo _notifyVo, INotifyTriggerType _trigger) {
        super("EXCEPTION-NOTIFY" + (_notifyVo != null ? "-" + _notifyVo.getTitle() : ""));
        notifyVo = _notifyVo;
        notifyTriggerType = _trigger;
    }

    @Override
    protected void execute() {
        try {
            NotifyPolicyVo notifyPolicyVo = notifyMapper.getNotifyPolicyByHandlerLimitOne(ExceptionNotifyPolicyHandler.class.getName());
            if (notifyPolicyVo != null) {
                NotifyPolicyConfigVo policyConfig = notifyPolicyVo.getConfig();
                if (policyConfig != null) {
                    String notifyPolicyHandler = notifyPolicyVo.getHandler();
                    NotifyPolicyUtil.execute(notifyPolicyHandler, notifyTriggerType, ExceptionMessageHandler.class, notifyPolicyVo, null, null, null, notifyVo, null, "");
                }
            }

        } catch (Exception ex) {
            logger.error("通知失败：" + ex.getMessage(), ex);
        }
    }

}
