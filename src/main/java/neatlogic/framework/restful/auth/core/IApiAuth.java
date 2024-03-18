/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.restful.auth.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.restful.dto.ApiVo;

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
