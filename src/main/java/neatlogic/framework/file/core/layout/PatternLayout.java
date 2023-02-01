/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.file.core.layout;

import ch.qos.logback.core.CoreConstants;
import neatlogic.framework.file.core.IEvent;

public class PatternLayout extends PatternLayoutBase<IEvent> {

    public String doLayout(IEvent event) {
//        System.out.println("b2");
        if (!isStarted()) {
            return CoreConstants.EMPTY_STRING;
        }
        return event.getFormattedMessage();
    }
}
