package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

@SuppressWarnings("serial")
public class SavePathNotExistsException extends ApiRuntimeException {
    public SavePathNotExistsException(String belong) {
        super("附件归属：{0}没有配置附件保存路径，请先配置", belong);
    }

}
