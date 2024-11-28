package neatlogic.framework.exception.elasticsearch;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ElasticSearchIndexNotFoundException extends ApiRuntimeException {
    public ElasticSearchIndexNotFoundException(String name) {
        super("Elasticsearch索引“{0}”不存在", name);
    }
}
