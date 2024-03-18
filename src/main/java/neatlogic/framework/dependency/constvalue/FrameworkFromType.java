/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.dependency.constvalue;

import neatlogic.framework.dependency.core.IFromType;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

/**
 * 被引用者（上游）类型
 *
 * @author: linbq
 * @since: 2021/4/2 10:30
 **/
public enum FrameworkFromType implements IFromType {
    MATRIX("matrix", new I18n("矩阵")),
    MATRIXATTR("matrixattr", new I18n("矩阵属性")),
    FORM("form", new I18n("表单")),
    FORMSCENE("formscene", new I18n("表单场景")),
    FORMATTR("formattr", new I18n("表单属性")),
    INTEGRATION("integration", new I18n("集成")),
    CMDBCI("cmdbci", new I18n("cmdb模型")),
    CMDBCIATTR("cmdbciattr", new I18n("cmdb模型属性")),
    WORKTIME("worktime", new I18n("服务窗口")),
    NOTIFY_POLICY("notifypolicy", new I18n("通知策略"));

    private String value;
    private I18n text;

    FrameworkFromType(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getText() {
        return $.t(text.toString());
    }
}
