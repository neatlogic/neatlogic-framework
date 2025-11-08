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

package neatlogic.framework.dto.license;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.restful.annotation.EntityField;

import java.io.Serializable;
import java.util.List;

public class LicenseInvalidVo implements Serializable {
    @EntityField(name = "模块组", type = ApiParamType.JSONARRAY)
    private List<ModuleGroupVo> moduleGroupVos;
    @EntityField(name = "类型", type = ApiParamType.STRING)
    private String type;
    @EntityField(name = "信息", type = ApiParamType.STRING)
    private String msg;

    public LicenseInvalidVo( List<ModuleGroupVo>  moduleGroupVos, String type, String msg) {
        this.moduleGroupVos = moduleGroupVos;
        this.type = type;
        this.msg = msg;
    }

    public List<ModuleGroupVo> getModuleGroupVos() {
        return moduleGroupVos;
    }

    public void setModuleGroupVos(List<ModuleGroupVo> moduleGroupVos) {
        this.moduleGroupVos = moduleGroupVos;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
