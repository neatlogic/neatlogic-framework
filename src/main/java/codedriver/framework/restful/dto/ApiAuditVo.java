package codedriver.framework.restful.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.restful.annotation.ExcelField;
import codedriver.framework.util.SnowflakeUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

public class ApiAuditVo extends BasePageVo {

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

	@EntityField(name = "参数内容hash", type = ApiParamType.STRING)
	private String paramHash;
	@EntityField(name = "错误内容hash", type = ApiParamType.STRING)
	private String errorHash;
	@EntityField(name = "结果内容hash", type = ApiParamType.STRING)
	private String resultHash;

	@EntityField(name = "API所属模块", type = ApiParamType.STRING)
	@ExcelField(name = "API所属模块")
	private String moduleGroup;
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

	private transient String logPath;
	private transient String tenant;

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

	public String getParamHash() {
		if (StringUtils.isBlank(paramHash) && StringUtils.isNotBlank(param)) {
			paramHash = DigestUtils.md5DigestAsHex(param.getBytes());
		}
		return paramHash;
	}

	public void setParamHash(String paramHash) {
		this.paramHash = paramHash;
	}

	public String getErrorHash() {
		if (StringUtils.isBlank(errorHash) && StringUtils.isNotBlank(error)) {
			errorHash = DigestUtils.md5DigestAsHex(error.getBytes());
		}
		return errorHash;
	}

	public void setErrorHash(String errorHash) {
		this.errorHash = errorHash;
	}

	public String getResultHash() {
		if (StringUtils.isBlank(resultHash) && result != null) {
			resultHash = DigestUtils.md5DigestAsHex(JSON.toJSONString(result).getBytes());
		}
		return resultHash;
	}

	public void setResultHash(String resultHash) {
		this.resultHash = resultHash;
	}

	public String getModuleGroup() {
		return moduleGroup;
	}

	public void setModuleGroup(String moduleGroup) {
		this.moduleGroup = moduleGroup;
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
}
