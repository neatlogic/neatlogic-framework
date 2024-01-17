package neatlogic.framework.exception.core;

public class ApiFieldValidException extends ApiRuntimeException {
    public ApiFieldValidException(String msg) {
        super("nfec.apifieldvalidexception.apifieldvalidexception", msg);
    }
}
