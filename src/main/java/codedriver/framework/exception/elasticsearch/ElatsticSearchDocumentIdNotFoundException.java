package codedriver.framework.exception.elasticsearch;

public class ElatsticSearchDocumentIdNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -8402781750711525455L;

    public ElatsticSearchDocumentIdNotFoundException() {
		super("找不到documentId");
	}
}
