package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class FileTooLargeException extends ApiRuntimeException {
    public FileTooLargeException(Long fileSize, Long maxSize) {
        super("附件最大支持：{1}字节，当前文件：{0}字节", maxSize, fileSize);
    }

}
