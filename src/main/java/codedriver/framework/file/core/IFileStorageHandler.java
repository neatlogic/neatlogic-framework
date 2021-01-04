package codedriver.framework.file.core;

import java.io.InputStream;

public interface IFileStorageHandler {

	public String getName();

	public String saveData(String tenantUuid, InputStream inputStream, String fileId, String contentType, String fileType) throws Exception;

	public InputStream getData(String path) throws Exception;

	public void deleteData(String filePath) throws Exception;

	public long getDataLength(String filePath) throws Exception;
}