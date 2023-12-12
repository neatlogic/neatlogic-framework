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
