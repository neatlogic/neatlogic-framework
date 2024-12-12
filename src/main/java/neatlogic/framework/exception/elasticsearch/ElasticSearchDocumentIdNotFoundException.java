package neatlogic.framework.exception.elasticsearch;

public class ElasticSearchDocumentIdNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8402781750711525455L;

    public ElasticSearchDocumentIdNotFoundException() {
		super("找不到documentId");
	}
}
