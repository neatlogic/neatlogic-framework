package codedriver.framework.common.dto;

import java.util.Date;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

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

}
