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

package neatlogic.framework.datawarehouse.dto;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.datawarehouse.enums.ExpireUnit;
import neatlogic.framework.datawarehouse.enums.Mode;
import neatlogic.framework.datawarehouse.enums.Status;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataSourceVo extends BasePageVo {
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "common.uniquename", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "common.name", type = ApiParamType.STRING)
    private String label;
    @EntityField(name = "common.description", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.xml", type = ApiParamType.STRING)
    private String xml;
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.cron", type = ApiParamType.STRING)
    private String cronExpression;
    @EntityField(name = "common.isactive", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.querytimeout", type = ApiParamType.LONG)
    private Integer queryTimeout;
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.fileid", type = ApiParamType.LONG)
    private Long fileId;
    @EntityField(name = "common.status", type = ApiParamType.ENUM, member = Status.class)
    private String status;
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.mode", type = ApiParamType.ENUM, member = Mode.class)
    private String mode;
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.datacount", type = ApiParamType.INTEGER)
    private Integer dataCount;
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.moduleid", type = ApiParamType.STRING)
    private String moduleId;

    @EntityField(name = "nfdd.datasourcevo.entityfield.name.modulename", type = ApiParamType.STRING)
    private String moduleName;
    @JSONField(serialize = false)
    private Integer expireMinute;//所有过期时间都转换成分钟
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.expireunit", type = ApiParamType.INTEGER)
    private Integer expireCount;
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.expireunit", type = ApiParamType.ENUM, member = ExpireUnit.class)
    private String expireUnit;
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.fieldlist", type = ApiParamType.JSONARRAY)
    private List<DataSourceFieldVo> fieldList = new ArrayList<>();//需要默认值为空数组，避免空指针异常
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.connectionid", type = ApiParamType.LONG)
    private Long connectionId;
    @JSONField(serialize = false)//数据列表
    private List<DataSourceDataVo> dataList;
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.paramlist", type = ApiParamType.JSONARRAY)
    private List<DataSourceParamVo> paramList = new ArrayList<>();//需要默认值为空数组，避免空指针异常
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.dbtype", type = ApiParamType.STRING)
    private String dbType;


    public DataSourceVo() {

    }

    public DataSourceVo(Long id) {
        this.id = id;
    }

    public void addField(List<DataSourceFieldVo> fieldList) {
        if (this.fieldList == null) {
            this.fieldList = new ArrayList<>();
        }
        this.fieldList.addAll(fieldList);
    }

    public void addField(DataSourceFieldVo field) {
        if (this.fieldList == null) {
            this.fieldList = new ArrayList<>();
        }
        this.fieldList.add(field);
    }

    public void addParam(List<DataSourceParamVo> paramList) {
        if (this.paramList == null) {
            this.paramList = new ArrayList<>();
        }
        this.paramList.addAll(paramList);
    }

    public String getModuleName() {
        if (StringUtils.isNotBlank(moduleId) && StringUtils.isBlank(moduleName)) {
            ModuleGroupVo groupVo = ModuleUtil.getModuleGroup(moduleId);
            if (groupVo != null) {
                moduleName = groupVo.getGroupName();
            }
        }
        return moduleName;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public void addParam(DataSourceParamVo param) {
        if (this.paramList == null) {
            this.paramList = new ArrayList<>();
        }
        this.paramList.add(param);
    }

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public Integer getExpireMinute() {
        if (expireCount != null && StringUtils.isNotBlank(expireUnit)) {
            if (expireUnit.equals(ExpireUnit.MINUTE.getValue())) {
                expireMinute = expireCount;
            } else if (expireUnit.equals(ExpireUnit.HOUR.getValue())) {
                expireMinute = expireCount * 60;
            } else if (expireUnit.equals(ExpireUnit.DAY.getValue())) {
                expireMinute = expireCount * 60 * 24;
            }
        }
        return expireMinute;
    }

    public void setExpireMinute(Integer expireMinute) {
        this.expireMinute = expireMinute;
    }

    public Integer getExpireCount() {
        return expireCount;
    }

    public void setExpireCount(Integer expireCount) {
        this.expireCount = expireCount;
    }

    public String getExpireUnit() {
        return expireUnit;
    }

    public void setExpireUnit(String expireUnit) {
        this.expireUnit = expireUnit;
    }

    @JSONField(serialize = false)
    public String getTableName() {
        return TenantContext.get().getDataDbName() + ".`datasource_" + this.getId() + "`";
    }

    public Integer getQueryTimeout() {
        return queryTimeout;
    }

    public List<Long> getKeyList() {
        if (CollectionUtils.isNotEmpty(fieldList)) {
            List<Long> keyList = new ArrayList<>();
            for (DataSourceFieldVo fieldVo : fieldList) {
                if (fieldVo.getIsKey().equals(1)) {
                    keyList.add(fieldVo.getId());
                }
            }
            return keyList;
        }
        return null;
    }

    public DataSourceFieldVo getFieldById(Long fieldId) {
        Optional<DataSourceFieldVo> op = fieldList.stream().filter(d -> d.getId().equals(fieldId)).findFirst();
        return op.orElse(null);
    }

    public Integer getDataCount() {
        return dataCount;
    }

    public void setDataCount(Integer dataCount) {
        this.dataCount = dataCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        if (StringUtils.isNotBlank(status)) {
            return Status.getText(status);
        }
        return null;
    }

    public Long getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(Long connectionId) {
        this.connectionId = connectionId;
    }

    public void setQueryTimeout(Integer queryTimeout) {
        this.queryTimeout = queryTimeout;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public List<DataSourceFieldVo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<DataSourceFieldVo> fieldList) {
        this.fieldList = fieldList;
    }

    public List<DataSourceParamVo> getParamList() {
        return paramList;
    }

    public void setParamList(List<DataSourceParamVo> paramList) {
        this.paramList = paramList;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
}
