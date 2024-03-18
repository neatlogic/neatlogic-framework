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

import neatlogic.framework.notify.dto.NotifyVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author lvzk
 * @since 2023/04/21 15:52
 **/
@Component
public class ExceptionCallerHandler extends ExceptionNotifyParamHandlerBase {
    static Logger logger = LoggerFactory.getLogger(ExceptionCallerHandler.class);
    @Override
    public String getValue() {
        return ExceptionNotifyParam.EXCEPTIONCALLER.getValue();
    }

    @Override
    public Object getMyText(NotifyVo notifyVo) {
        try {
            return notifyVo.getCallerMessageHandlerClass().newInstance().getCallerMessage(notifyVo);
        }catch (Exception ex){
            logger.error(ex.getMessage(),ex);
            return StringUtils.EMPTY;
        }
    }
}
