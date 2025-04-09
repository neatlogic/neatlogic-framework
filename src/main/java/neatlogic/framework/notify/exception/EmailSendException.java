package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class EmailSendException extends ApiRuntimeException {
    public EmailSendException(Exception ex) {
        super("nfne.emailsendexception.emailsendexception", ex.getMessage());
    }
    public EmailSendException(String message, String allRecipients) {
        super("nfne.emailsendexception.emailsendexception_a", message, allRecipients);
    }
}
