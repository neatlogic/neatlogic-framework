/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.common.dto;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.dto.UserVo;
import codedriver.framework.restful.annotation.EntityField;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class BaseEditorVo extends BasePageVo {
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
    @EntityField(name = "创建者额外属性", type = ApiParamType.STRING)
    private String fcuInfo;
    @EntityField(name = "创建者头像", type = ApiParamType.STRING)
    private String fcuAvatar;
    @EntityField(name = "创建者VIP等级", type = ApiParamType.INTEGER)
    private Integer fcuVipLevel;
    @EntityField(name = "修改者额外属性", type = ApiParamType.STRING)
    private String lcuInfo;
    @EntityField(name = "修改者头像", type = ApiParamType.STRING)
    private String lcuAvatar;
    @EntityField(name = "修改者VIP等级", type = ApiParamType.INTEGER)
    private Integer lcuVipLevel;
    @JSONField(serialize = false)
    public String tenantUuid;//当前租户uuid

    @EntityField(name = "创建者VO")
    private UserVo fcuVo;
    @EntityField(name = "修改者VO")
    private UserVo lcuVo;

    public BaseEditorVo() {
    }

    public String getTenantUuid() {
        return TenantContext.get().getTenantUuid();
    }


    public String getFcu() {
        if (StringUtils.isBlank(fcu)) {
            fcu = UserContext.get().getUserUuid();
        }
        return fcu;
    }

    public void setFcu(String fcu) {
        this.fcu = fcu;
    }

    public String getFcuName() {
        return fcuName;
    }

    public void setFcuName(String fcuName) {
        this.fcuName = fcuName;
    }

    public Date getFcd() {
        return fcd;
    }

    public void setFcd(Date fcd) {
        this.fcd = fcd;
    }

    public String getLcu() {
        if (StringUtils.isBlank(lcu)) {
            lcu = UserContext.get().getUserUuid();
        }
        return lcu;
    }

    public void setLcu(String lcu) {
        this.lcu = lcu;
    }

    public String getLcuName() {
        return lcuName;
    }

    public void setLcuName(String lcuName) {
        this.lcuName = lcuName;
    }

    public Date getLcd() {
        return lcd;
    }

    public void setLcd(Date lcd) {
        this.lcd = lcd;
    }

    public String getFcuInfo() {
        return fcuInfo;
    }

    public void setFcuInfo(String fcuInfo) {
        this.fcuInfo = fcuInfo;
    }

    public String getFcuAvatar() {
        if (StringUtils.isBlank(fcuAvatar) && StringUtils.isNotBlank(fcuInfo)) {
            JSONObject jsonObject = JSONObject.parseObject(fcuInfo);
            fcuAvatar = jsonObject.getString("avatar");
        }
        return fcuAvatar;
    }

    public String getLcuInfo() {
        return lcuInfo;
    }

    public void setLcuInfo(String lcuInfo) {
        this.lcuInfo = lcuInfo;
    }

    public String getLcuAvatar() {
        if (StringUtils.isBlank(lcuAvatar) && StringUtils.isNotBlank(lcuInfo)) {
            JSONObject jsonObject = JSONObject.parseObject(lcuInfo);
            lcuAvatar = jsonObject.getString("avatar");
        }
        return lcuAvatar;
    }

    public Integer getFcuVipLevel() {
        return fcuVipLevel;
    }

    public void setFcuVipLevel(Integer fcuVipLevel) {
        this.fcuVipLevel = fcuVipLevel;
    }

    public Integer getLcuVipLevel() {
        return lcuVipLevel;
    }

    public void setLcuVipLevel(Integer lcuVipLevel) {
        this.lcuVipLevel = lcuVipLevel;
    }

    public UserVo getFcuVo() {
        return fcuVo;
    }

    public void setFcuVo(UserVo fcuVo) {
        this.fcuVo = fcuVo;
    }

    public UserVo getLcuVo() {
        return lcuVo;
    }

    public void setLcuVo(UserVo lcuVo) {
        this.lcuVo = lcuVo;
    }
}
