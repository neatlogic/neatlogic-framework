package neatlogic.framework.exception.qdrant;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class QdrantCollectionNotFoundException extends ApiRuntimeException {
    public QdrantCollectionNotFoundException(String name) {
        super("Qdrant集合“{0}”不存在", name);
    }
}
