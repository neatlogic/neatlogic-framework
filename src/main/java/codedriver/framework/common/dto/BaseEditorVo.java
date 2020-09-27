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
	@EntityField(name = "修改者额外属性", type = ApiParamType.STRING)
	private String userInfo;
	@EntityField(name = "修改者头像", type = ApiParamType.STRING)
	private String avatar;

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

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public String getAvatar() {
		if (StringUtils.isBlank(avatar) && StringUtils.isNotBlank(userInfo)) {
			JSONObject jsonObject = JSONObject.parseObject(userInfo);
			avatar = jsonObject.getString("avatar");
		}
		return avatar;
	}
}
