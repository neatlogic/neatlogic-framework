package neatlogic.framework.notify.dto.job;

import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.scheduler.dto.JobAuditVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class NotifyJobAuditVo extends JobAuditVo {

	@EntityField(name = "收件人vo列表")
	private JSONArray toVoList;

	@EntityField(name = "状态")
	private JSONObject statusVo;

	public JSONArray getToVoList() {
		return toVoList;
	}

	public void setToVoList(JSONArray toVoList) {
		this.toVoList = toVoList;
	}

	public JSONObject getStatusVo() {
		return statusVo;
	}

	public void setStatusVo(JSONObject statusVo) {
		this.statusVo = statusVo;
	}
}
