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
