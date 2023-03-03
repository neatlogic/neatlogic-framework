/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
