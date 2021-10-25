/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.auth.core;

import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface IApiAuth {

    String getType();

    int auth(ApiVo interfaceVo, JSONObject jsonParam, HttpServletRequest request) throws IOException;

    JSONObject help();

    /**
     * 根据输入的认证信息生成认证header
     *
     * @param jsonObj 用户输入的认证信息
     * @return 认证头部信息
     */
    JSONObject createHeader(JSONObject jsonObj);

}
