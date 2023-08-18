package neatlogic.framework.dto.plugin.issue;

import neatlogic.framework.common.dto.BasePageVo;

import java.io.Serializable;

/**
 * @author Yu
 */
public class SearchCommitVo extends BasePageVo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Long repositoryId;
    private String issueNo;
    private Long mrId;

    public Long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(Long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getIssueNo() {
        return issueNo;
    }

    public void setIssueNo(String issueNo) {
        this.issueNo = issueNo;
    }

    public Long getMrId() {
        return mrId;
    }

    public void setMrId(Long mrId) {
        this.mrId = mrId;
    }

    @Override
    public String toString() {
        return "SearchCommitVo [repositoryId=" + repositoryId + ", issueNo=" + issueNo + ", mrId=" + mrId + "]";
    }

}