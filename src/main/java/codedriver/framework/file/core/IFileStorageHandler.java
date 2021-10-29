/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

import java.io.InputStream;

public interface IFileStorageHandler {

    String getName();

    String saveData(String tenantUuid, InputStream inputStream, String fileId, String contentType, String fileType) throws Exception;

    InputStream getData(String path) throws Exception;

    void deleteData(String filePath) throws Exception;

    long getDataLength(String filePath) throws Exception;

    boolean isExit(String filePath) throws Exception;
}
