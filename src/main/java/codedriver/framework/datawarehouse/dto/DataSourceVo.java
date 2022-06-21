/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.dto;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.datawarehouse.enums.ExpireUnit;
import codedriver.framework.datawarehouse.enums.Mode;
import codedriver.framework.datawarehouse.enums.Status;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataSourceVo extends BasePageVo {
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "唯一标识", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "名称", type = ApiParamType.STRING)
    private String label;
    @EntityField(name = "说明", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "xml配置", type = ApiParamType.STRING)
    private String xml;
    @EntityField(name = "定时策略", type = ApiParamType.STRING)
    private String cronExpression;
    @EntityField(name = "是否激活", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "查询超时时间，单位：秒", type = ApiParamType.LONG)
    private Integer queryTimeout;
    @EntityField(name = "配置文件id", type = ApiParamType.LONG)
    private Long fileId;
    @EntityField(name = "状态", type = ApiParamType.ENUM, member = Status.class)
    private String status;
    @EntityField(name = "同步模式", type = ApiParamType.ENUM, member = Mode.class)
    private String mode;
    @EntityField(name = "数据量", type = ApiParamType.INTEGER)
    private Integer dataCount;
    @JSONField(serialize = false)
    private Integer expireMinute;//所有过期时间都转换成分钟
    @EntityField(name = "有效时间数值", type = ApiParamType.INTEGER)
    private Integer expireCount;
    @EntityField(name = "有效时间单位", type = ApiParamType.ENUM, member = ExpireUnit.class)
    private String expireUnit;
    @EntityField(name = "字段列表", type = ApiParamType.JSONARRAY)
    private List<DataSourceFieldVo> fieldList = new ArrayList<>();//需要默认值为空数组，避免空指针异常
    @EntityField(name = "数据连接id", type = ApiParamType.LONG)
    private Long connectionId;
    @JSONField(serialize = false)//数据列表
    private List<DataSourceDataVo> dataList;
    @EntityField(name = "参数列表", type = ApiParamType.JSONARRAY)
    private List<DataSourceParamVo> paramList = new ArrayList<>();//需要默认值为空数组，避免空指针异常

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
}
