package neatlogic.framework.scheduler.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

public class JobDataVo {
    @EntityField(name = "common.jobuuid", type = ApiParamType.STRING)
    private String jobUuid;
    @EntityField(name = "common.name", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "common.value", type = ApiParamType.STRING)
    private String value;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJobUuid() {
        return jobUuid;
    }

    public void setJobUuid(String jobUuid) {
        this.jobUuid = jobUuid;
    }
}
