package neatlogic.framework.notify.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class NotifyTemplateReferencedCannotBeDeletedException extends ApiRuntimeException {

    private static final long serialVersionUID = 7199905421971800695L;

    public NotifyTemplateReferencedCannotBeDeletedException(String templateId) {
        super("通知模板：“{0}”有被引用，不能删除", templateId);
    }
}
