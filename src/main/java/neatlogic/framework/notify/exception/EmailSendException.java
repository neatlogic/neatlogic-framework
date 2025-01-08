package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class EmailSendException extends ApiRuntimeException {
    public EmailSendException(Exception ex) {
        super("nfne.emailsendexception.emailsendexception", ex.getMessage());
    }
}
