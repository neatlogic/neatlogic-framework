/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.common.dto;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.dto.UserVo;
import neatlogic.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.annotation.JSONField;
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
    @EntityField(name = "创建者", type = ApiParamType.STRING)
    private String fcu;
    @EntityField(name = "创建者中文名", type = ApiParamType.STRING)
    private String fcuName;
    @EntityField(name = "创建日期", type = ApiParamType.STRING)
    private Date fcd;
    @EntityField(name = "修改者", type = ApiParamType.STRING)
    private String lcu;
    @EntityField(name = "修改者中文名", type = ApiParamType.STRING)
    private String lcuName;
    @EntityField(name = "修改日期", type = ApiParamType.STRING)
    private Date lcd;
    @JSONField(serialize = false)
    public String tenantUuid;//当前租户uuid

    @EntityField(name = "创建者VO")
    private UserVo fcuVo;
    @EntityField(name = "修改者VO")
    private UserVo lcuVo;

    @EntityField(name = "操作类型，创建或修改")
    private String actionType;

    public BaseEditorVo() {
    }

    public String getTenantUuid() {
        return TenantContext.get().getTenantUuid();
    }


    public final String getFcu() {
        if (StringUtils.isBlank(fcu)) {
            fcu = UserContext.get().getUserUuid();
        }
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
        if (StringUtils.isBlank(lcu)) {
            lcu = UserContext.get().getUserUuid();
        }
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
            actionType = "创建";
        } else {
            actionType = "修改";
        }
        return actionType;
    }
}
