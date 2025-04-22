package neatlogic.framework.exception.elasticsearch;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ElasticSearchHostNotFoundException extends ApiRuntimeException {
    public ElasticSearchHostNotFoundException() {
        super("请配置Elasticsearch访问地址");
    }
}
