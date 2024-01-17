/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.crossover;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface IFileCrossoverService extends ICrossoverService {
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
