package neatlogic.framework.exception.elasticsearch;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ElatsticSearchHandlerNotFoundException extends ApiRuntimeException {

    private static final long serialVersionUID = 8358695524151979636L;

    public ElatsticSearchHandlerNotFoundException(String handler) {
        super("exception.framework.elatsticsearchhandlernotfoundexception", handler);
    }
}
