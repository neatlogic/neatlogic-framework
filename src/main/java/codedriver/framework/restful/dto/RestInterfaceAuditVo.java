package codedriver.framework.restful.dto;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.common.util.FileWorker;

public class RestInterfaceAuditVo extends BasePageVo {
	private Long id;
	private Integer interfaceId;
	private String ip;
	private String param;
	private String startTime;
	private String endTime;
	private Long timeCost;
	private String status;
	private String statusText;
	private String error;
	private String timeCostText;
	private String result;
	private String paramPath;
	private String errorPath;
	private String resultPath;

	public RestInterfaceAuditVo() {
		this.setPageSize(20);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getInterfaceId() {
		return interfaceId;
	}

	public void setInterfaceId(Integer interfaceId) {
		this.interfaceId = interfaceId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getParam() {
		if (param == null) {
			if (StringUtils.isNotBlank(this.paramPath)) {
				if (this.paramPath.contains("||")) {
					String[] pathArray = this.paramPath.split("\\|\\|");
					if (pathArray.length == 2) {
						param = FileWorker.readZipContent(pathArray[0], pathArray[1]);
					} else {
						param = "压缩文件路径不完整";
					}
				} else {
					param = FileWorker.readContent(this.paramPath);
					;
				}

				if (StringUtils.isNotBlank(param)) {
					param = param.trim();
					if (param.startsWith("{")) {
						try {
							JSONObject jsonObj = JSONObject.parseObject(param);
							param = jsonObj.toJSONString();
						} catch (Exception ex) {
						}
					} else if (param.startsWith("[")) {
						try {
							JSONArray jsonList = JSONArray.parseArray(param);
							param = jsonList.toJSONString();
						} catch (Exception ex) {
						}
					}
				}
			}
		}
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getError() {
		if (error == null) {
			if (StringUtils.isNotBlank(this.errorPath)) {
				if (this.errorPath.contains("||")) {
					String[] pathArray = this.errorPath.split("\\|\\|");
					if (pathArray.length == 2) {
						error = FileWorker.readZipContent(pathArray[0], pathArray[1]);
					} else {
						error = "压缩文件路径不完整";
					}
				} else {
					error = FileWorker.readContent(this.errorPath);
					;
				}
			}
		}
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getStatusText() {
		if (this.status.equals("succeed")) {
			statusText = "成功";
		} else if (this.status.equals("failed")) {
			statusText = "失败";
		}
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public Long getTimeCost() {
		return timeCost;
	}

	public void setTimeCost(Long timeCost) {
		this.timeCost = timeCost;
	}

	public String getTimeCostText() {
		if (timeCost != null) {
			if (timeCost / 1000 > 0) {
				timeCostText = timeCost / 1000 + "秒";
			} else {
				timeCostText = timeCost + "毫秒";
			}
		}
		return timeCostText;
	}

	public void setTimeCostText(String timeCostText) {
		this.timeCostText = timeCostText;
	}

	public String getResultPath() {
		return resultPath;
	}

	public void setResultPath(String resultPath) {
		this.resultPath = resultPath;
	}

	public String getParamPath() {
		return paramPath;
	}

	public void setParamPath(String paramPath) {
		this.paramPath = paramPath;
	}

	public String getErrorPath() {
		return errorPath;
	}

	public void setErrorPath(String errorPath) {
		this.errorPath = errorPath;
	}

	public String getResult() {
		if (result == null) {
			if (StringUtils.isNotBlank(this.resultPath)) {
				if (this.resultPath.contains("||")) {
					String[] pathArray = this.resultPath.split("\\|\\|");
					if (pathArray.length == 2) {
						result = FileWorker.readZipContent(pathArray[0], pathArray[1]);
					} else {
						result = "压缩文件路径不完整";
					}
				} else {
					result = FileWorker.readContent(this.resultPath);
					;
				}

				if (StringUtils.isNotBlank(result)) {
					result = result.trim();
					if (result.startsWith("{")) {
						try {
							JSONObject jsonObj = JSONObject.parseObject(result);
							result = jsonObj.toJSONString(4);
						} catch (Exception ex) {
						}
					} else if (result.startsWith("[")) {
						try {
							JSONArray jsonList = JSONArray.parseArray(result);
							result = jsonList.toJSONString(4);
						} catch (Exception ex) {
						}
					}
				}
			}
		}

		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
