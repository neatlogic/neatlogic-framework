/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

public class LicenseVo implements Serializable {
    private static final long serialVersionUID = -4515626151148587123L;
    //客户名
    private String purchaser;
    //数据库链接
    private String dbUrl;
    //到期时间
    private Date expireTime;
    //签发时间
    private Date issueTime;
    //到期后仍可以使用天数
    private Integer expiredDay = 30;
    //即将到期前提醒天数
    private Integer willExpiredDay = 30;
    //创建时间
    private Date createTime;
    @JSONField(serialize = false)
    //加密后的license
    private String licenseStr;
    //授权模块
    private JSONObject modulePackage;

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isExpiredOutOfDay(Long diffTime) {
        if (diffTime < 0) {
            return (-diffTime / (1000L * 24 * 60 * 60)) >= expiredDay;
        }
        return false;
    }

    public Long getCurrentWillExpiredDay(Long diffTime) {
        if (diffTime > 0) {
            long day = diffTime / (1000L * 24 * 60 * 60);
            if (day < willExpiredDay)
                return willExpiredDay - day;
        }
        return null;
    }

    public Long getCurrentExpireDay(Long diffTime) {
        if (diffTime < 0) {
            long day = -diffTime / (1000L * 24 * 60 * 60);
            if (day < expiredDay)
                return expiredDay - day;
        }
        return null;
    }

    public String getLicenseStr() {
        return licenseStr;
    }

    public void setLicenseStr(String licenseStr) {
        this.licenseStr = licenseStr;
    }

    public Integer getExpiredDay() {
        return expiredDay;
    }

    public void setExpiredDay(Integer expiredDay) {
        this.expiredDay = expiredDay;
    }

    public Integer getWillExpiredDay() {
        return willExpiredDay;
    }

    public void setWillExpiredDay(Integer willExpiredDay) {
        this.willExpiredDay = willExpiredDay;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String purchaser) {
        this.purchaser = purchaser;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    public JSONObject getModulePackage() {
        return modulePackage;
    }

    public void setModulePackage(JSONObject modulePackage) {
        this.modulePackage = modulePackage;
    }
}
