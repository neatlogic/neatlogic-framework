package codedriver.framework.common.dto;

import java.util.Date;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

public class BaseEditorVo extends BasePageVo {
	@EntityField(name = "创建者", type = ApiParamType.STRING)
	private String fcu;
	@EntityField(name = "创建者中文名", type = ApiParamType.STRING)
	private String fcuName;
	@EntityField(name = "创建日期", type = ApiParamType.STRING)
	private Date fcd;
	@EntityField(name = "修改者", type = ApiParamType.STRING)
	private String lcu;
	@EntityField(name = "修改者中文名", type = ApiParamType.STRING)
	private String lcuName;
	@EntityField(name = "修改日期", type = ApiParamType.STRING)
	private Date lcd;
	@EntityField(name = "创建者额外属性", type = ApiParamType.STRING)
	private String fcuInfo;
	@EntityField(name = "创建者头像", type = ApiParamType.STRING)
	private String fcuAvatar;
	@EntityField(name = "修改者额外属性", type = ApiParamType.STRING)
	private String lcuInfo;
	@EntityField(name = "修改者头像", type = ApiParamType.STRING)
	private String lcuAvatar;

	public BaseEditorVo() {
	}

	public String getFcu() {
		return fcu;
	}

	public void setFcu(String fcu) {
		this.fcu = fcu;
	}

	public String getFcuName() {
		return fcuName;
	}

	public void setFcuName(String fcuName) {
		this.fcuName = fcuName;
	}

	public Date getFcd() {
		return fcd;
	}

	public void setFcd(Date fcd) {
		this.fcd = fcd;
	}

	public String getLcu() {
		return lcu;
	}

	public void setLcu(String lcu) {
		this.lcu = lcu;
	}

	public String getLcuName() {
		return lcuName;
	}

	public void setLcuName(String lcuName) {
		this.lcuName = lcuName;
	}

	public Date getLcd() {
		return lcd;
	}

	public void setLcd(Date lcd) {
		this.lcd = lcd;
	}

	public String getFcuInfo() {
		return fcuInfo;
	}

	public void setFcuInfo(String fcuInfo) {
		this.fcuInfo = fcuInfo;
	}

	public String getFcuAvatar() {
		if (StringUtils.isBlank(fcuAvatar) && StringUtils.isNotBlank(fcuInfo)) {
			JSONObject jsonObject = JSONObject.parseObject(fcuInfo);
			fcuAvatar = jsonObject.getString("avatar");
		}
		return fcuAvatar;
	}

	public String getLcuInfo() {
		return lcuInfo;
	}

	public void setLcuInfo(String lcuInfo) {
		this.lcuInfo = lcuInfo;
	}

	public String getLcuAvatar() {
		if (StringUtils.isBlank(lcuAvatar) && StringUtils.isNotBlank(lcuInfo)) {
			JSONObject jsonObject = JSONObject.parseObject(lcuInfo);
			lcuAvatar = jsonObject.getString("avatar");
		}
		return lcuAvatar;
	}

}
