/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.fulltextindex.dto.fulltextindex;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.fulltextindex.enums.Status;
import codedriver.framework.restful.annotation.EntityField;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class FullTextIndexRebuildAuditVo {
    @EntityField(name = "类型", type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "类型名称", type = ApiParamType.STRING)
    private String typeName;
    @EntityField(name = "状态", type = ApiParamType.ENUM, member = Status.class)
    private String status = Status.DONE.getValue();
    @EntityField(name = "状态名称", type = ApiParamType.STRING)
    private String statusText;
    @EntityField(name = "开始时间", type = ApiParamType.LONG)
    private Date startTime;
    @EntityField(name = "结束时间", type = ApiParamType.LONG)
    private Date endTime;
    @EntityField(name = "服务器id", type = ApiParamType.INTEGER)
    private Integer serverId;
    @EntityField(name = "发起人uuid", type = ApiParamType.STRING)
    private String editor;
    @EntityField(name = "异常", type = ApiParamType.STRING)
    private String error;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getServerId() {
        if (serverId == null) {
            serverId = Config.SCHEDULE_SERVER_ID;
        }
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        if (StringUtils.isBlank(statusText) && StringUtils.isNotBlank(status)) {
            statusText = Status.getText(status);
        }
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getEditor() {
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
