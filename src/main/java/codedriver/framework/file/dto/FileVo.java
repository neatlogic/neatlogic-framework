package codedriver.framework.file.dto;

import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

public class FileVo {
	@EntityField(name = "附件id", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "附件保存路径", type = ApiParamType.STRING)
	private String path;
	@EntityField(name = "附件名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "附件大小（字节）", type = ApiParamType.INTEGER)
	private Long size;
	@EntityField(name = "上传用户", type = ApiParamType.STRING)
	private String userUuid;
	@EntityField(name = "上传时间", type = ApiParamType.STRING)
	private String uploadTime;
	@EntityField(name = "附件归属", type = ApiParamType.STRING)
	private String type;
	@EntityField(name = "扩展名", type = ApiParamType.STRING)
	private String ext;
	@EntityField(name = "内容类型", type = ApiParamType.STRING)
	private String contentType;

	public Long getId() {
		if (id == null) {
			id = SnowflakeUtil.uniqueLong();
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
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

	public String getExt() {
		if (StringUtils.isNotBlank(name) && name.indexOf(".") > -1) {
			ext = name.substring(name.lastIndexOf(".") + 1);
		}
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
