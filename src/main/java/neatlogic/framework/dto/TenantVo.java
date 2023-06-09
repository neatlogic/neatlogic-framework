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

package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.$;
import neatlogic.framework.util.SnowflakeUtil;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TenantVo extends BasePageVo {
    private static final long serialVersionUID = 5037087043421533431L;

    public enum Status {
        BUILDING("building", "创建中"),
        BUILT("built", "已完成"),
        DDL("ddl", "执行ddl"),
        DML("dml", "执行dml"),
        DML_DEMO("dmldemo", "enum.master.tenantvo.status.dmldemo"),
        ERROR("error", "异常");

        private String value;
        private String text;

        Status(String value, String text) {
            this.value = value;
            this.text = text;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static String getText(String value){
            for (Status status : Status.values()){
                if(Objects.equals(status.getValue(),value)){
                    return status.getText();
                }
            }
            return null;
        }

        public String getText() {
            return $.t(text);
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "名称", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "是否激活", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "描述", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "有效期限", type = ApiParamType.LONG)
    private Date expireDate;
    @EntityField(name = "激活模块", type = ApiParamType.JSONARRAY)
    private List<ModuleVo> moduleList;
    @EntityField(name = "激活模块分组", type = ApiParamType.JSONARRAY)
    private List<ModuleGroupVo> moduleGroupList;
    @EntityField(name = "状态", type = ApiParamType.STRING)
    private String status;
    @EntityField(name = "超级管理员", type = ApiParamType.JSONARRAY)
    private List<String> superAdminList;
    @EntityField(name = "异常信息", type = ApiParamType.STRING)
    private String errorMsg;
    @EntityField(name = "租户mongodb", type = ApiParamType.JSONOBJECT)
    private MongoDbVo mongodb;
    @EntityField(name = "数据库数据源", type = ApiParamType.JSONOBJECT)
    private DatasourceVo datasource;
    @EntityField(name = "数据库数据源", type = ApiParamType.JSONOBJECT)
    private boolean isLocalDb = true;
    public TenantVo() {
        this.setPageSize(20);
    }

    public TenantVo(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public List<ModuleVo> getModuleList() {
        return moduleList;
    }

    public void setModuleList(List<ModuleVo> moduleList) {
        this.moduleList = moduleList;
    }

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ModuleGroupVo> getModuleGroupList() {
        return moduleGroupList;
    }

    public void setModuleGroupList(List<ModuleGroupVo> moduleGroupList) {
        this.moduleGroupList = moduleGroupList;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusText() {
        return Status.getText(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getSuperAdminList() {
        return superAdminList;
    }

    public void setSuperAdminList(List<String> superAdminList) {
        this.superAdminList = superAdminList;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public MongoDbVo getMongodb() {
        return mongodb;
    }

    public void setMongodb(MongoDbVo mongodb) {
        this.mongodb = mongodb;
    }

    public DatasourceVo getDatasource() {
        return datasource;
    }

    public void setDatasource(DatasourceVo datasource) {
        this.datasource = datasource;
    }

    public boolean getIsLocalDb() {
        return isLocalDb;
    }

    public void setIsLocalDb(boolean localDb) {
        isLocalDb = localDb;
    }
}
