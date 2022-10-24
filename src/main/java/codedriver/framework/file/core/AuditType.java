/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

public enum AuditType implements IAuditType {
    API_AUDIT("apiaudit", "apiaudit.log", "10mb");
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
