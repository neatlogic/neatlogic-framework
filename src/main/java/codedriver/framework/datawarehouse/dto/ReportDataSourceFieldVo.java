/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.datawarehouse.enums.FieldInputType;
import codedriver.framework.datawarehouse.enums.FieldType;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

public class ReportDataSourceFieldVo extends BasePageVo {
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "数据源id", type = ApiParamType.LONG)
    private Long dataSourceId;
    @EntityField(name = "唯一标识", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "名称", type = ApiParamType.STRING)
    private String label;
    @EntityField(name = "类型", type = ApiParamType.ENUM, member = FieldType.class)
    private String type;
    @EntityField(name = "类型名称", type = ApiParamType.STRING)
    private String typeText;
    @EntityField(name = "输入方式", type = ApiParamType.ENUM, member = FieldInputType.class)
    private String inputType;
    @EntityField(name = "输入方式名称", type = ApiParamType.STRING)
    private String inputTypeText;
    @EntityField(name = "是否主键", type = ApiParamType.INTEGER)
    private Integer isKey;
    @EntityField(name = "配置", type = ApiParamType.JSONOBJECT)
    private JSONObject config;
    @JSONField(serialize = false)
    private String configStr;
    @JSONField(serialize = false)
    private Object value;//值,作为条件

    public ReportDataSourceFieldVo() {

    }

    public ReportDataSourceFieldVo(ReportDataSourceFieldVo reportDataSourceFieldVo) {
        this.name = reportDataSourceFieldVo.getName();
        this.label = reportDataSourceFieldVo.getLabel();
        this.type = reportDataSourceFieldVo.getType();
        this.id = reportDataSourceFieldVo.getId();
    }

    public ReportDataSourceFieldVo(String name, String label, String type, Integer isKey) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.isKey = isKey;
    }

    public String getTypeText() {
        if (type != null && StringUtils.isBlank(typeText)) {
            typeText = FieldType.getText(type);
        }
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public String getInputTypeText() {
        if (inputType != null && StringUtils.isBlank(inputTypeText)) {
            inputTypeText = FieldInputType.getText(inputType);
        }
        return inputTypeText;
    }

    public void setInputTypeText(String inputTypeText) {
        this.inputTypeText = inputTypeText;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Integer getIsKey() {
        return isKey;
    }

    public void setIsKey(Integer isKey) {
        this.isKey = isKey;
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

    public Long getDataSourceId() {
        return dataSourceId;
    }

    public void setDataSourceId(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        if (StringUtils.isBlank(label)) {
            return name;
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        if (StringUtils.isBlank(type)) {
            return FieldType.TEXT.getValue();
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInputType() {
        if (StringUtils.isBlank(inputType)) {
            return FieldInputType.TEXT.getValue();
        }
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public String getConfigStr() {
        if (StringUtils.isBlank(configStr) && config != null) {
            configStr = config.toString();
        }
        return configStr;
    }

    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }
}
