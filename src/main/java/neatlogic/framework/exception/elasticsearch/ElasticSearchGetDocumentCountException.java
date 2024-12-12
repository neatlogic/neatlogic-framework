package neatlogic.framework.exception.elasticsearch;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ElasticSearchGetDocumentCountException extends ApiRuntimeException {


    public ElasticSearchGetDocumentCountException(Exception e) {
        super(e);
    }
}
