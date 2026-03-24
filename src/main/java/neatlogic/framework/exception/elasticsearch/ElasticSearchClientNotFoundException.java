package neatlogic.framework.exception.elasticsearch;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ElasticSearchClientNotFoundException extends ApiRuntimeException {
    public ElasticSearchClientNotFoundException() {
        super("无法初始化Elasticsearch客户端");
    }
}
