package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileAccessDeniedException extends ApiRuntimeException {
    public FileAccessDeniedException(String filename, String operation) {
        super("您没有权限{0}文件：{1}", operation, filename);
    }

}
