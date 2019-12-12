package codedriver.framework.file.dto;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

public class FileVo {
	private String uuid;
	private String path;
	private String name;
	private Long size;
	private String userId;
	private String uploadTime;
	private String type;

	public String getUuid() {
		if (StringUtils.isBlank(uuid)) {
			uuid = UUID.randomUUID().toString().replace("-", "");
		}
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(String uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
