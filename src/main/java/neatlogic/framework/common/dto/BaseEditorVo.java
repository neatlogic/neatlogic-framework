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
