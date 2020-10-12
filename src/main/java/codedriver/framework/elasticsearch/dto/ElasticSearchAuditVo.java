package codedriver.framework.elasticsearch.dto;

import java.util.Date;

public class ElasticSearchAuditVo {
    private Long documentId;
    private String handler;
    private Date udpateTime;

    public ElasticSearchAuditVo() {

    }

    public ElasticSearchAuditVo(String handler, Long documentId) {
        this.handler = handler;
        this.documentId = documentId;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Date getUdpateTime() {
        return udpateTime;
    }

    public void setUdpateTime(Date udpateTime) {
        this.udpateTime = udpateTime;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

}
