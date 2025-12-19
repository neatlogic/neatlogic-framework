/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.file.core;

import neatlogic.framework.file.dto.FileVo;

import java.io.InputStream;

public interface IFileStorageHandler {

    String getName();

    String saveData(String tenantUuid, InputStream inputStream, FileVo file) throws Exception;

    /**
     * 上传文件到固定路径
     * @param inputStream 流
     * @param contentType 文件类型
     * @param filePath 目标路径
     * @return
     * @throws Exception
     */
    String saveData(InputStream inputStream, String contentType, String filePath) throws Exception;

    InputStream getData(String filePath) throws Exception;

    void deleteData(String filePath) throws Exception;

    long getDataLength(String filePath) throws Exception;

    boolean isExit(String filePath) throws Exception;
}
