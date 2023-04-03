package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class FileTypeNotSupportToExportException extends ApiRuntimeException {
    private static final long serialVersionUID = 754389697658227783L;

    public FileTypeNotSupportToExportException(String type) {
        super("exception.framework.filetypenotsupporttoexportexception", type);
    }

}
