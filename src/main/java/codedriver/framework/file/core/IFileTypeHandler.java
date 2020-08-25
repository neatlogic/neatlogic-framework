package codedriver.framework.file.core;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.file.dto.FileVo;

public interface IFileTypeHandler {
	/**
	 * 文件权限校验的方法区，校验的参数可用reqMap来传递，reqMap的参数值来自httprequest对象。
	 */
	public boolean valid(String userUuid, JSONObject jsonObj);

	public String getName();

	public String getDisplayName();

	public void afterUpload(FileVo fileVo, JSONObject jsonObj);
}
