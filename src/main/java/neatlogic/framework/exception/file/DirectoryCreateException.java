package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class DirectoryCreateException extends ApiRuntimeException {
    public DirectoryCreateException(String filepath) {
        super("无法创建文件夹：{0}", filepath);
    }

}
