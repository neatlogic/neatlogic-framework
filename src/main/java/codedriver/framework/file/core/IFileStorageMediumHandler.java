package codedriver.framework.file.core;

import codedriver.framework.file.dto.FileVo;

import java.io.InputStream;

public interface IFileStorageMediumHandler {

	public String getName();

	public String saveData(String tenantUuid, InputStream inputStream, FileVo fileVo,String contentType) throws Exception;

	public InputStream getData(String path) throws Exception;
}
