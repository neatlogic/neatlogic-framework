package codedriver.framework.notify.dto.job;

import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.scheduler.dto.JobAuditVo;
import com.alibaba.fastjson.JSONArray;

public class NotifyJobAuditVo extends JobAuditVo {

	@EntityField(name = "收件人vo列表")
	private JSONArray toVoList;

	public JSONArray getToVoList() {
		return toVoList;
	}

	public void setToVoList(JSONArray toVoList) {
		this.toVoList = toVoList;
	}
}
