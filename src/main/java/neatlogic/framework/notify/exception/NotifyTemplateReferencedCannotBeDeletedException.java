package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyTemplateReferencedCannotBeDeletedException extends ApiRuntimeException {

    private static final long serialVersionUID = 7199905421971800695L;

    public NotifyTemplateReferencedCannotBeDeletedException(String templateId) {
        super("exception.framework.notifytemplatereferencedcannotbedeletedexception", templateId);
    }
}
