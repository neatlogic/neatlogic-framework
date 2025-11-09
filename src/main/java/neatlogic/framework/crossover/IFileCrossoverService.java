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

package neatlogic.framework.crossover;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.file.dto.FileVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IFileCrossoverService extends ICrossoverService {

    FileVo getFileById(Long id);

    void downloadFile(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 删除文件
     * @param fileId 文件id
     * @param paramObj 其他参数
     * @throws Exception
     */
    void deleteFile(Long fileId, JSONObject paramObj) throws Exception;


    /**
     * 读取服务器本地文件内容
     * @param path 路径
     * @param startIndex 开始下标
     * @param offset 读取内容字节数
     * @return 文件内容
     */
    JSONObject readLocalFile(String path, int startIndex, int offset);

    /**
     * 读取其他服务器文件内容
     * @param paramObj 入参
     * @param serverId 服务器ID
     * @return 文件内容
     */
    JSONObject readRemoteFile(JSONObject paramObj, Integer serverId);

    /**
     * 下载当前服务器文件
     * @param path
     * @param startIndex
     * @param offset
     * @param response
     */
    void downloadLocalFile(String path, int startIndex, int offset, HttpServletResponse response);

    /**
     * 下载另一个服务器文件
     * @param paramObj
     * @param serverId
     * @param request
     * @param response
     * @throws IOException
     */
    void downloadRemoteFile(JSONObject paramObj, Integer serverId, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
