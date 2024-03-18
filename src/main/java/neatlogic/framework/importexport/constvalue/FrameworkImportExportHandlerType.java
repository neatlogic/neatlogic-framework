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

package neatlogic.framework.importexport.constvalue;

import neatlogic.framework.importexport.core.ImportExportHandlerType;
import neatlogic.framework.util.$;

public enum FrameworkImportExportHandlerType implements ImportExportHandlerType {
    FILE("file", "附件"),
    INTEGRATION("integration", "集成"),
    FORM("form", "表单"),
    MATRIX("matrix", "矩阵"),
    NOTIFY_POLICY("notifyPolicy", "通知策略"),
    CMDB_CI("cmdbCi", "配置项模型"),
    AUTOEXEC_COMBOP("autoexecCombop", "组合工具"),
    ;

    private String value;
    private String text;

    FrameworkImportExportHandlerType(String value, String text) {
        this.value = value;
        this.text = text;
    }
    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public String getText() {
        return $.t(this.text);
    }
}
