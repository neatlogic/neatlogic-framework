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

package neatlogic.module.framework.restful.api;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.auth.label.NoAuth;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.Description;
import neatlogic.framework.restful.annotation.OperationType;
import neatlogic.framework.restful.annotation.Output;
import neatlogic.framework.restful.annotation.Param;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.framework.util.PasswordRSAUtil;
import org.springframework.stereotype.Service;

/**
 * 提供全局密码RSA公钥，供需要加密密码的前端页面统一调用。
 */
@Service
@AuthAction(action = NoAuth.class)
@OperationType(type = OperationTypeEnum.SEARCH)
public class GetPasswordPublicKeyApi extends PrivateApiComponentBase {

    @Override
    public String getToken() {
        return "password/publickey/get";
    }

    @Override
    public String getName() {
        return "nmfra.getpasswordpublickeyapi.getname";
    }

    @Override
    public String getConfig() {
        return null;
    }


    @Output({
            @Param(name = "publicKey", type = ApiParamType.STRING, desc = "common.rsapublickey"),
    })
    @Description(desc = "nmfra.getpasswordpublickeyapi.getname")
    @Override
    public Object myDoService(JSONObject paramObj) {
        JSONObject resultObj = new JSONObject();
        // 公钥使用X.509 SPKI Base64格式，可直接供浏览器Web Crypto导入。
        resultObj.put("publicKey", PasswordRSAUtil.getPublicKey());
        return resultObj;
    }
}
