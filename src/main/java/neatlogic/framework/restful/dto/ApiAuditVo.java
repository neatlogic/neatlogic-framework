/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.restful.dto;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.audit.AuditVoHandler;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.restful.annotation.ExcelField;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ApiAuditVo extends BasePageVo implements AuditVoHandler {

    public final static String SUCCEED = "succeed";
    public final static String FAILED = "failed";

    @EntityField(name = "主键", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "地址", type = ApiParamType.STRING)
    @ExcelField(name = "token")
    private String token;
    @EntityField(name = "用户ID", type = ApiParamType.STRING)
    private String userUuid;
    @EntityField(name = "认证方式", type = ApiParamType.STRING)
    @ExcelField(name = "认证方式")
    private String authtype;
    @EntityField(name = "服务器ID", type = ApiParamType.STRING)
    @ExcelField(name = "服务器ID")
    private Integer serverId;
    @EntityField(name = "用户IP", type = ApiParamType.STRING)
    @ExcelField(name = "用户IP")
    private String ip;
    @EntityField(name = "开始时间", type = ApiParamType.LONG)
    @ExcelField(name = "开始时间")
    private Date startTime;
    @EntityField(name = "结束时间", type = ApiParamType.LONG)
    @ExcelField(name = "结束时间")
    private Date endTime;
    @EntityField(name = "耗时", type = ApiParamType.LONG)
    @ExcelField(name = "耗时（毫秒）")
    private Long timeCost;
    @EntityField(name = "状态", type = ApiParamType.STRING)
    @ExcelField(name = "状态")
    private String status;
    @EntityField(name = "参数内容", type = ApiParamType.STRING)
    @ExcelField(name = "参数")
    private String param;
    @EntityField(name = "异常内容", type = ApiParamType.STRING)
    @ExcelField(name = "异常")
    private String error;
    @EntityField(name = "结果内容", type = ApiParamType.STRING)
    @ExcelField(name = "结果")
    private Object result;

    @EntityField(name = "参数内容文件位置", type = ApiParamType.STRING)
    private String paramFilePath;
    @EntityField(name = "结果内容文件位置", type = ApiParamType.STRING)
    private String resultFilePath;
    @EntityField(name = "错误内容文件位置", type = ApiParamType.STRING)
    private String errorFilePath;

    @EntityField(name = "参数内容文件路径ID", type = ApiParamType.LONG)
    private Long paramPathId;
    @EntityField(name = "结果内容文件位置ID", type = ApiParamType.LONG)
    private Long resultPathId;
    @EntityField(name = "错误内容文件位置ID", type = ApiParamType.LONG)
    private Long errorPathId;
    @EntityField(name = "参数内容文件路径", type = ApiParamType.JSONOBJECT)
    private ApiAuditPathVo paramPath;
    @EntityField(name = "结果内容文件位置", type = ApiParamType.JSONOBJECT)
    private ApiAuditPathVo resultPath;
    @EntityField(name = "错误内容文件位置", type = ApiParamType.JSONOBJECT)
    private ApiAuditPathVo errorPath;

    @EntityField(name = "API所属模块", type = ApiParamType.STRING)
    private String moduleGroup;
    @EntityField(name = "模块group名称", type = ApiParamType.STRING)
    @ExcelField(name = "API所属模块")
    private String moduleGroupName;
    @EntityField(name = "API所属功能", type = ApiParamType.STRING)
    private String funcId;
    @EntityField(name = "操作类型", type = ApiParamType.STRING)
    private String operationType;
    @EntityField(name = "用户名", type = ApiParamType.STRING)
    @ExcelField(name = "用户名")
    private String userName;
    @EntityField(name = "API中文名", type = ApiParamType.STRING)
    @ExcelField(name = "API中文名")
    private String apiName;
    @EntityField(name = "tokenList", type = ApiParamType.STRING)
    private List<String> tokenList;
    @EntityField(name = "排序类型(desc|asc)", type = ApiParamType.STRING)
    private String orderType;
    @EntityField(name = "时间跨度", type = ApiParamType.INTEGER)
    private Integer timeRange;
    @EntityField(name = "时间跨度单位(day|month)", type = ApiParamType.STRING)
    private String timeUnit;
    @JSONField(serialize = false)
    private String logPath;
    @JSONField(serialize = false)
    private String tenant;
    @JSONField(serialize = false)
    private List<String> userUuidList;
    @JSONField(serialize = false)
    private List<String> statusList;

    public ApiAuditVo() {
        this.setPageSize(20);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getAuthtype() {
        return authtype;
    }

    public void setAuthtype(String authtype) {
        this.authtype = authtype;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModuleGroup() {
        return moduleGroup;
    }

    public void setModuleGroup(String moduleGroup) {
        this.moduleGroup = moduleGroup;
    }

    public String getModuleGroupName() {
        if (StringUtils.isBlank(moduleGroupName) && StringUtils.isNotBlank(moduleGroup)) {
            ModuleGroupVo group = ModuleUtil.getModuleGroupMap().get(moduleGroup);
            if (group != null) {
                String groupName = group.getGroupName();
                if (StringUtils.isNotBlank(groupName)) {
                    moduleGroupName = groupName;
                }
            }
        }
        return moduleGroupName;
    }

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public List<String> getTokenList() {
        return tokenList;
    }

    public void setTokenList(List<String> tokenList) {
        this.tokenList = tokenList;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Integer getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(Integer timeRange) {
        this.timeRange = timeRange;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
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

    public Long getParamPathId() {
        return paramPathId;
    }

    public void setParamPathId(Long paramPathId) {
        this.paramPathId = paramPathId;
    }

    public Long getResultPathId() {
        return resultPathId;
    }

    public void setResultPathId(Long resultPathId) {
        this.resultPathId = resultPathId;
    }

    public Long getErrorPathId() {
        return errorPathId;
    }

    public void setErrorPathId(Long errorPathId) {
        this.errorPathId = errorPathId;
    }

    public ApiAuditPathVo getParamPath() {
        return paramPath;
    }

    public void setParamPath(ApiAuditPathVo paramPath) {
        this.paramPath = paramPath;
    }

    public ApiAuditPathVo getResultPath() {
        return resultPath;
    }

    public void setResultPath(ApiAuditPathVo resultPath) {
        this.resultPath = resultPath;
    }

    public ApiAuditPathVo getErrorPath() {
        return errorPath;
    }

    public void setErrorPath(ApiAuditPathVo errorPath) {
        this.errorPath = errorPath;
    }
}
