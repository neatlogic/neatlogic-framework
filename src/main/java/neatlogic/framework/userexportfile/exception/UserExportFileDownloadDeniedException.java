package neatlogic.framework.userexportfile.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

import java.io.Serial;

public class UserExportFileDownloadDeniedException extends ApiRuntimeException {

    @Serial
    private static final long serialVersionUID = 3196317808541689166L;

    public UserExportFileDownloadDeniedException(String name) {
        super("您没有权限下载{0}文件");
    }

}
