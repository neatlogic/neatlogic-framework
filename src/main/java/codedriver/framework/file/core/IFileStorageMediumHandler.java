package codedriver.framework.file.core;

import java.io.InputStream;

public interface IFileStorageMediumHandler {

	public String getName();

	public String saveData(String tenantUuid, InputStream inputStream, Long fileId,String contentType,String fileType) throws Exception;

	public InputStream getData(String path) throws Exception;

	public void deleteData(String filePath) throws Exception;
}
