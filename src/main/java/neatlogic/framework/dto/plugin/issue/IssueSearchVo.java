package neatlogic.framework.dto.plugin.issue;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.List;

public class IssueSearchVo {

    /**
     * 项目列表，由projectKey得来
     */
    @EntityField(name = "项目列表，由projectKey得来", type = ApiParamType.JSONARRAY)
    private List<String> project;
    /**
     * 类型
     */
    @EntityField(name = "类型", type = ApiParamType.JSONARRAY)
    private List<String> type;
    /**
     * 状态
     */
    @EntityField(name = "状态", type = ApiParamType.JSONARRAY)
    private List<String> status;

    @EntityField(name = "同步源对象", type = ApiParamType.JSONARRAY)
    private SyncSourceVo syncSourceVo;


    public List<String> getProject() {
        return project;
    }

    public void setProject(List<String> project) {
        this.project = project;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public List<String> getStatus() {
        return status;
    }

    public void setStatus(List<String> status) {
        this.status = status;
    }

    public SyncSourceVo getSyncSourceVo() {
        return syncSourceVo;
    }

    public void setSyncSourceVo(SyncSourceVo syncSourceVo) {
        this.syncSourceVo = syncSourceVo;
    }
}
