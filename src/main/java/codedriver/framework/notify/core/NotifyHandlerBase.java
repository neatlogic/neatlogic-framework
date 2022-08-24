/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.notify.core;

import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.notify.dto.NotifyVo;
import codedriver.module.framework.notify.exception.ExceptionNotifyThread;
import codedriver.module.framework.notify.exception.ExceptionNotifyTriggerType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NotifyHandlerBase implements INotifyHandler {

    private static final Logger logger = LoggerFactory.getLogger(NotifyHandlerBase.class);

    public final boolean execute(NotifyVo notifyVo) throws Exception {

        if (StringUtils.isNotBlank(notifyVo.getError())) {
            logger.error(notifyVo.getError());
            if (notifyVo.getIsSendExceptionNotify() == 1) {
                notifyVo.setIsSendExceptionNotify(0);// 防止循环调用NotifyPolicyUtil.execute方法
                CachedThreadPool.execute(new ExceptionNotifyThread(notifyVo, ExceptionNotifyTriggerType.EMAILNOTIFYEXCEPTION));
            }
            return false;
        } else {
            return myExecute(notifyVo);
        }

    }

    protected abstract boolean myExecute(NotifyVo notifyVo) throws Exception;
}
