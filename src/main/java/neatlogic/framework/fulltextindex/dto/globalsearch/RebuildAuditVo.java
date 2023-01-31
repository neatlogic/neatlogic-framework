/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.fulltextindex.dto.globalsearch;

import org.apache.commons.lang3.StringUtils;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;

public class RebuildAuditVo {
	public enum Status {
		DOING("doing", "重建中"), DONE("done", "重建完毕");
		private String value;
		private String name;

		private Status(String _value, String _name) {
			this.value = _value;
			this.name = _name;
		}

		public String getValue() {
			return value;
		}

		public String getName() {
			return name;
		}

		public static String getName(String v) {
			for (Status s : Status.values()) {
				if (s.getValue().equals(v)) {
					return s.getName();
				}
			}
			return "";
		}
	}

	private String type;
	private String startTime;
	private String endTime;
	private String status;
	private String statusText;
	private String editor;
	private String editorName;
	private Integer serverId;

	public RebuildAuditVo() {

	}

	public RebuildAuditVo(String _type, String _status) {
		type = _type;
		status = _status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getStatusText() {
		if (StringUtils.isNotBlank(status)) {
			statusText = Status.getName(status);
		}
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getEditor() {
		if (StringUtils.isBlank(editor)) {
			editor = UserContext.get().getUserUuid();
		}
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getEditorName() {
		return editorName;
	}

	public void setEditorName(String editorName) {
		this.editorName = editorName;
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

}
