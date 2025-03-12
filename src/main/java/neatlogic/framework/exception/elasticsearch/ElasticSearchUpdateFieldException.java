package neatlogic.framework.exception.elasticsearch;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ElasticSearchUpdateFieldException extends ApiRuntimeException {


    public ElasticSearchUpdateFieldException(String docId, String field, String message) {
        super("ES修改文档“{0}”的属性“{1}”失败，异常：{2}", docId, field, message);
    }
}
