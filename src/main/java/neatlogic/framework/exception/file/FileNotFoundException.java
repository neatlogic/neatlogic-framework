package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;

public class FileNotFoundException extends ApiRuntimeException {

    public enum Type {
        NONEXISTENT,
        DIRECTORY,
        ;
    }

    public FileNotFoundException(Long id) {
        super("nfef.filenotfoundexception.filenotfoundexception", id);
    }

    public FileNotFoundException(Type type, String path) {
        super(getMessage(type, path));
    }

    private static String getMessage(Type type, String path) {
        if (type == Type.NONEXISTENT) {
            return $.t("nfef.filenotfoundexception.getmessage.nonexistent", path);
        } else if (type == Type.DIRECTORY) {
            return $.t("nfef.filenotfoundexception.getmessage.directory", path);
        }
        return StringUtils.EMPTY;
    }
}
