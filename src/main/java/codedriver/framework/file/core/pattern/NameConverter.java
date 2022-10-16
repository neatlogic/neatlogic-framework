/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.pattern;

import codedriver.framework.file.core.IEvent;

public class NameConverter extends ClassicConverter {
    @Override
    public String convert(IEvent event) {
        return event.getName();
    }
}
