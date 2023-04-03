package neatlogic.framework.exception.core;

public class ApiFieldValidNotFoundException extends ApiRuntimeException {
    public ApiFieldValidNotFoundException(String msg) {
        super("exception.framework.apifieldvalidnotfoundexception", msg);
    }
}
