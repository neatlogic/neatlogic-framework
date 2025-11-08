/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
