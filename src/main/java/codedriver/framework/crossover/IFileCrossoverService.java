/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.crossover;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

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
     * 获取encode文件名
     * @param request 请求
     * @param fileName 文件名
     * @return 文件名
     */
    String getFileNameEncode(HttpServletRequest request, String fileName) throws UnsupportedEncodingException;
}
