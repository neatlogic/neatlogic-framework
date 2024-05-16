package neatlogic.framework.integration.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.audit.AuditVoHandler;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class IntegrationAuditVo extends BasePageVo implements AuditVoHandler {
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @JSONField(serialize = false)
    private List<Long> idList;
    @EntityField(name = "集成配置uuid", type = ApiParamType.STRING)
    private String integrationUuid;
    @EntityField(name = "集成配置uuid列表", type = ApiParamType.JSONARRAY)
    private List<String> integrationUuidList;
    @EntityField(name = "用户uuid", type = ApiParamType.STRING)
    private String userUuid;
    @EntityField(name = "用户名", type = ApiParamType.STRING)
    private String userName;
    @EntityField(name = "请求来源", type = ApiParamType.STRING)
    private String requestFrom;
    @JSONField(serialize = false)
    private Integer serverId;
    @EntityField(name = "开始时间", type = ApiParamType.LONG)
    private Date startTime;
    @EntityField(name = "结束时间", type = ApiParamType.LONG)
    private Date endTime;
    @EntityField(name = "耗时（毫秒）", type = ApiParamType.LONG)
    private Long timeCost;
    @EntityField(name = "状态", type = ApiParamType.STRING)
    private String status;
    @EntityField(name = "请求参数", type = ApiParamType.STRING)
    private String param;
    @EntityField(name = "返回结果", type = ApiParamType.STRING)
    private Object result;
    @EntityField(name = "异常", type = ApiParamType.STRING)
    private String error;
    @EntityField(name = "参数内容文件路径", type = ApiParamType.STRING)
    private String paramFilePath;
    @EntityField(name = "结果内容文件路径", type = ApiParamType.STRING)
    private String resultFilePath;
    @EntityField(name = "错误内容文件路径", type = ApiParamType.STRING)
    private String errorFilePath;
    @JSONField(serialize = false)
    private List<String> userUuidList;
    @JSONField(serialize = false)
    private List<String> statusList;
    @EntityField(name = "请求头", type = ApiParamType.JSONOBJECT)
    private JSONObject headers;
    @EntityField(name = "请求头字符串", type = ApiParamType.STRING)
    private String headersStr;

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIntegrationUuid() {
        return integrationUuid;
    }

    public List<String> getIntegrationUuidList() {
        return integrationUuidList;
    }

    public void setIntegrationUuidList(List<String> integrationUuidList) {
        this.integrationUuidList = integrationUuidList;
    }

    public void setIntegrationUuid(String integrationUuid) {
        this.integrationUuid = integrationUuid;
    }

    public String getUserUuid() {
        if (StringUtils.isNotBlank(userUuid)) {
            if (userUuid.contains("#")) {
                userUuid = userUuid.split("#")[1];
            }
        }
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
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

    public Long getTimeCost() {
        if (timeCost == null) {
            if (startTime != null && endTime != null) {
                timeCost = endTime.getTime() - startTime.getTime();
            }
        }
        return timeCost;
    }

    public void setTimeCost(Long timeCost) {
        this.timeCost = timeCost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void appendError(String error) {
        if (StringUtils.isNotBlank(error)) {
            if (StringUtils.isNotBlank(this.error)) {
                this.error += "\n" + error;
            } else {
                this.error = error;
            }
        }
    }

    public String getRequestFrom() {
        return requestFrom;
    }

    public void setRequestFrom(String requestFrom) {
        this.requestFrom = requestFrom;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getParamFilePath() {
        return paramFilePath;
    }

    public void setParamFilePath(String paramFilePath) {
        this.paramFilePath = paramFilePath;
    }

    public String getResultFilePath() {
        return resultFilePath;
    }

    public void setResultFilePath(String resultFilePath) {
        this.resultFilePath = resultFilePath;
    }

    public String getErrorFilePath() {
        return errorFilePath;
    }

    public void setErrorFilePath(String errorFilePath) {
        this.errorFilePath = errorFilePath;
    }

    public List<String> getUserUuidList() {
        if (CollectionUtils.isNotEmpty(userUuidList)) {
            userUuidList = userUuidList.stream().map(GroupSearch::removePrefix).collect(Collectors.toList());
        }
        return userUuidList;
    }

    public void setUserUuidList(List<String> userUuidList) {
        this.userUuidList = userUuidList;
    }

    public List<String> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<String> statusList) {
        this.statusList = statusList;
    }

    public JSONObject getHeaders() {
        return headers;
    }

    public void setHeaders(JSONObject headers) {
        this.headers = headers;
    }

    public String getHeadersStr() {
        if (MapUtils.isNotEmpty(headers)) {
            return headers.toJSONString();
        }
        return null;
    }
}
