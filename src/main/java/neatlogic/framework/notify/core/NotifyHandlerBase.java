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

package neatlogic.framework.notify.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.notify.dto.NotifyVo;
import neatlogic.module.framework.notify.exception.ExceptionNotifyThread;
import neatlogic.module.framework.notify.exception.ExceptionNotifyTriggerType;
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
                notifyVo.appendError("<br>data：<xmp>");
                notifyVo.appendError(JSON.toJSONString(notifyVo.getData(), SerializerFeature.PrettyFormat));
                notifyVo.appendError("</xmp><br>titleTemplate：<xmp>");
                notifyVo.appendError(notifyVo.getTemplateTitle());
                notifyVo.appendError("</xmp><br>contentTemplate：<xmp>");
                notifyVo.appendError(notifyVo.getTemplateContent());
                notifyVo.appendError("</xmp><br>");
                CachedThreadPool.execute(new ExceptionNotifyThread(notifyVo, ExceptionNotifyTriggerType.EMAILNOTIFYEXCEPTION));
            }
            return false;
        } else {
            return myExecute(notifyVo);
        }

    }

    protected abstract boolean myExecute(NotifyVo notifyVo) throws Exception;
}
