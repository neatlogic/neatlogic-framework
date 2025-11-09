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

package neatlogic.framework.file.core;

public enum AuditType implements IAuditType {
    INTEGRATION_AUDIT("integrationaudit", "integrationaudit.log", "30mb"),
    API_AUDIT("apiaudit", "apiaudit.log", "30mb");
    private String type;
    private String fileName;
    private String maxFileSize;

    AuditType(
            String type,
            String fileName,
            String maxFileSize) {
        this.type = type;
        this.fileName = fileName;
        this.maxFileSize = maxFileSize;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getMaxFileSize() {
        return maxFileSize;
    }
}
