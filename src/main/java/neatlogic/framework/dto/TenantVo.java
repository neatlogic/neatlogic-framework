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

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TenantVo extends BaseEditorVo {
    private static final long serialVersionUID = 5037087043421533431L;

    public enum Status {
        BUILDING("building", "nfdt.status.building"),
        BUILT("built", "nfdt.status.built"),
        DDL("ddl", "ddl"),
        DDL_DEMO_DATA("ddldemo_data", "ddl demo data"),
        DML("dml", "dml"),
        DML_DEMO("dmldemo", "dml demo"),
        DML_DEMO_DATA("dmldemo_data", "dml demo data"),
        MONGODB("mongodb", "mongodb"),
        ERROR("error", "nfdt.status.error");

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

        public static String getText(String value) {
            for (Status status : Status.values()) {
                if (Objects.equals(status.getValue(), value)) {
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
    @EntityField(name = "uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "common.name", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "common.isactive", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "common.description", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "nfd.tenantvo.expiredate", type = ApiParamType.LONG)
    private Date expireDate;
    @EntityField(name = "nfd.tenantvo.modulelist", type = ApiParamType.JSONARRAY)
    private List<ModuleVo> moduleList;
    @EntityField(name = "nfd.tenantvo.modulegrouplist", type = ApiParamType.JSONARRAY)
    private List<ModuleGroupVo> moduleGroupList;
    @EntityField(name = "common.status", type = ApiParamType.STRING)
    private String status;
    @EntityField(name = "common.superadmin", type = ApiParamType.JSONARRAY)
    private List<String> superAdminList;
    @EntityField(name = "common.errormsg", type = ApiParamType.STRING)
    private String errorMsg;
    @EntityField(name = "nfd.tenantvo.mongodb", type = ApiParamType.JSONOBJECT)
    private MongoDbVo mongodb;
    @EntityField(name = "nfd.tenantvo.authmongodb", type = ApiParamType.JSONOBJECT)
    private MongoDbVo authMongodb;
    @JSONField(serialize = false)
    @EntityField(name = "nfd.tenantvo.authmongodb", type = ApiParamType.STRING)
    private String authConfig;
    @EntityField(name = "common.datasource", type = ApiParamType.JSONOBJECT)
    private DatasourceVo datasource;
    @EntityField(name = "nfd.tenantvo.islocaldb", type = ApiParamType.JSONOBJECT)
    private boolean isLocalDb = true;

    @JSONField(serialize = false)
    @EntityField(name = "nfd.tenantvo.superadmin", type = ApiParamType.JSONOBJECT)
    private UserVo superAdmin;

    @EntityField(name = "nfd.tenantvo.visittime", type = ApiParamType.LONG)
    private Date visitTime;

    @EntityField(name = "创建租户时是否需要demo数据", type = ApiParamType.INTEGER)
    private Integer isNeedDemo;

    private Boolean isEdit = false;


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

    public MongoDbVo getAuthMongodb() {
        if (this.authMongodb == null && StringUtils.isNotBlank(authConfig)) {
            this.authMongodb = JSONObject.parseObject(authConfig, MongoDbVo.class);
        }
        return authMongodb;
    }

    public void setAuthMongodb(MongoDbVo authMongodb) {
        this.authMongodb = authMongodb;
    }

    public String getAuthConfig() {
        if (StringUtils.isBlank(authConfig) && authMongodb != null) {
            authConfig = JSONObject.toJSONString(authMongodb);
        }
        return authConfig;
    }

    public void setAuthConfig(String authConfig) {
        this.authConfig = authConfig;
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

    public UserVo getSuperAdmin() {
        return superAdmin;
    }

    public void setSuperAdmin(UserVo superAdmin) {
        this.superAdmin = superAdmin;
    }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

    public Integer getIsNeedDemo() {
        return isNeedDemo;
    }

    public void setIsNeedDemo(Integer isNeedDemo) {
        this.isNeedDemo = isNeedDemo;
    }

    public Boolean getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(Boolean isEdit) {
        this.isEdit = isEdit;
    }
}
