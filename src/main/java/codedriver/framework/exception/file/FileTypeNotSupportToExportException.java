package codedriver.framework.exception.file;

import codedriver.framework.exception.core.ApiRuntimeException;

public class FileTypeNotSupportToExportException extends ApiRuntimeException {
    private static final long serialVersionUID = 754389697658227783L;

    public FileTypeNotSupportToExportException(String type) {
        super("不支持导出" + type + "格式的文件");
    }

}
