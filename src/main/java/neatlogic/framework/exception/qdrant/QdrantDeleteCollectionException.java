package neatlogic.framework.exception.qdrant;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class QdrantDeleteCollectionException extends ApiRuntimeException {
    public QdrantDeleteCollectionException(Exception ex) {
        super(ex);
    }
}
