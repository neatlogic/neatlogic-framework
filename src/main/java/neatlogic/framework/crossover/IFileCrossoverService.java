/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
