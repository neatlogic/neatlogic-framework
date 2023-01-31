/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.module.framework.notify.exception;

import neatlogic.framework.asynchronization.thread.CodeDriverThread;
import neatlogic.framework.notify.core.INotifyTriggerType;
import neatlogic.framework.notify.dao.mapper.NotifyMapper;
import neatlogic.framework.notify.dto.NotifyPolicyConfigVo;
import neatlogic.framework.notify.dto.NotifyPolicyVo;
import neatlogic.framework.notify.dto.NotifyVo;
import neatlogic.framework.util.NotifyPolicyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;

@Service
public class ExceptionNotifyThread extends CodeDriverThread {
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
                    NotifyPolicyUtil.execute(notifyPolicyHandler, notifyTriggerType, null, notifyPolicyVo, null, null, null, notifyVo, null, "");
                }
            }

        } catch (Exception ex) {
            logger.error("通知失败：" + ex.getMessage(), ex);
        }
    }

}
