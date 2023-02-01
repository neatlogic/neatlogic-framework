/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.crossover;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IFileCrossoverService extends ICrossoverService {
    void downloadFile(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * 删除文件
     * @param fileId 文件id
     * @param paramObj 其他参数
     * @throws Exception
     */
    void deleteFile(Long fileId, JSONObject paramObj) throws Exception;
}
