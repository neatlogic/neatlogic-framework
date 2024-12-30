package neatlogic.framework.exception.elasticsearch;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ElasticSearchDeleteFieldException extends ApiRuntimeException {


    public ElasticSearchDeleteFieldException(String docId, String field, String message) {
        super("ES删除文档“{0}”的属性“{1}”失败，异常：{2}", docId, field, message);
    }
}
