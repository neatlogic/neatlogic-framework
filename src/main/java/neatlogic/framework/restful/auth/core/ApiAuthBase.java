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

package neatlogic.framework.restful.auth.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiVo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public abstract class ApiAuthBase implements IApiAuth {
    protected static ApiMapper apiMapper;

    @Autowired
    public void setApiMapper(ApiMapper _apiMapper) {
        apiMapper = _apiMapper;
    }

    @Override
    public int auth(ApiVo interfaceVo, JSONObject jsonParam, HttpServletRequest request) throws IOException {
        return myAuth(interfaceVo,jsonParam,request);
    }

    public abstract int myAuth(ApiVo interfaceVo, JSONObject jsonParam, HttpServletRequest request) throws IOException;


}
