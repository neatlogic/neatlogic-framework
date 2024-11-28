package neatlogic.framework.exception.elasticsearch;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ElasticSearchCreateDocumentException extends ApiRuntimeException {


    public ElasticSearchCreateDocumentException(Exception e) {
        super(e);
    }
}
