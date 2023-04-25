/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import java.io.PrintWriter;
import java.io.StringWriter;

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
