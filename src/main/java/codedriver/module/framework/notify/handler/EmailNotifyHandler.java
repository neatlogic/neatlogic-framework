/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.notify.handler;

import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.notify.core.NotifyHandlerBase;
import codedriver.framework.notify.core.NotifyHandlerType;
import codedriver.framework.notify.dto.NotifyVo;
import codedriver.module.framework.notify.exception.ExceptionNotifyThread;
import codedriver.module.framework.notify.exception.ExceptionNotifyTriggerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @program: codedriver
 * @description:
 * @create: 2019-12-09 15:34
 **/
@Component
public class EmailNotifyHandler extends NotifyHandlerBase {

    private static Logger logger = LoggerFactory.getLogger(EmailNotifyHandler.class);

    @Override
    public void myExecute(NotifyVo notifyVo) {
        try {
            sendEmail(notifyVo, true);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if (notifyVo.getIsSendExceptionNotify() == 1) {
                notifyVo.setIsSendExceptionNotify(0);// 防止循环调用NotifyPolicyUtil.execute方法
                CachedThreadPool.execute(new ExceptionNotifyThread(notifyVo, e, ExceptionNotifyTriggerType.EMAILNOTIFYEXCEPTION));
            }
        }

    }

    @Override
    public String getName() {
        return NotifyHandlerType.EMAIL.getText();
    }


    @Override
    public String getType() {
        return NotifyHandlerType.EMAIL.getValue();
    }
}
