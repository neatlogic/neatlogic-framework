package neatlogic.framework.dto.plugin.issue;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

public class ProjectVo {
    /**
     * 项目key
     */
    @EntityField(name = "项目key", type = ApiParamType.STRING)
    private String projectKey;
    /**
     * 项目名称
     */
    @EntityField(name = "项目名称", type = ApiParamType.STRING)
    private String projectName;

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
