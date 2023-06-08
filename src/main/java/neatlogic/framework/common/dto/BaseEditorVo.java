/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.common.dto;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Objects;

/**
 * BaseEditorVo类的fcu、fcd、lcu、lcd字段，分别对应数据库表的fcu、fcd、lcu、lcd字段，建议fcu、lcu设置为必填。
 * 建议首次插入一条数据时，同时插入fcu、fcd、lcu、lcd四个字段值，其中fcu与lcu值相等，fcd与lcd值相等。
 * 之后修改数据时只需更新lcu与lcd字段值，fcu与fcd字段值不变。
 */
public class BaseEditorVo extends BasePageVo {
    private static final long serialVersionUID = -3871232273093802236L;
    @EntityField(name = "common.createuser", type = ApiParamType.STRING)
    private String fcu;
    @EntityField(name = "common.createusername", type = ApiParamType.STRING)
    private String fcuName;
    @EntityField(name = "common.createdate", type = ApiParamType.STRING)
    private Date fcd;
    @EntityField(name = "common.editor", type = ApiParamType.STRING)
    private String lcu;
    @EntityField(name = "common.editorname", type = ApiParamType.STRING)
    private String lcuName;
    @EntityField(name = "common.editdate", type = ApiParamType.STRING)
    private Date lcd;
    @JSONField(serialize = false)
    public String tenantUuid;//当前租户uuid

    @EntityField(name = "common.createrobject")
    private UserVo fcuVo;
    @EntityField(name = "common.editorobject")
    private UserVo lcuVo;

    @EntityField(name = "common.actiontype")
    private String actionType;

    public BaseEditorVo() {
    }

    public String getTenantUuid() {
        return TenantContext.get().getTenantUuid();
    }


    public final String getFcu() {
        return fcu;
    }

    public final void setFcu(String fcu) {
        this.fcu = fcu;
    }

    public String getFcuName() {
        return fcuName;
    }

    public void setFcuName(String fcuName) {
        this.fcuName = fcuName;
    }

    public final Date getFcd() {
        return fcd;
    }

    public final void setFcd(Date fcd) {
        this.fcd = fcd;
    }

    public final String getLcu() {
        return lcu;
    }

    public final void setLcu(String lcu) {
        this.lcu = lcu;
    }

    public String getLcuName() {
        return lcuName;
    }

    public void setLcuName(String lcuName) {
        this.lcuName = lcuName;
    }

    public final Date getLcd() {
        return lcd;
    }

    public final void setLcd(Date lcd) {
        this.lcd = lcd;
    }

    public final UserVo getFcuVo() {
        if (fcuVo == null && StringUtils.isNotBlank(fcu)) {
            fcuVo = new UserVo(fcu, false);
        }
        return fcuVo;
    }

    public final UserVo getLcuVo() {
        if (lcuVo == null && StringUtils.isNotBlank(lcu)) {
            lcuVo = new UserVo(lcu, false);
        }
        return lcuVo;
    }

    public final String getActionType() {
        if (Objects.equals(fcd, lcd)) {
            actionType = $.t("common.create");
        } else {
            actionType = $.t("common.edit");
        }
        return actionType;
    }
}
