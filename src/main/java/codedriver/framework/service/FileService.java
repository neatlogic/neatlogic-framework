package codedriver.framework.service;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FileService {
    void downloadFile(JSONObject paramObj, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
