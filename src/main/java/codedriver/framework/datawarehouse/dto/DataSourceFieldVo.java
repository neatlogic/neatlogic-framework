/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.datawarehouse.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.datawarehouse.condition.DatasourceConditionHandlerFactory;
import codedriver.framework.datawarehouse.condition.IDatasourceConditionHandler;
import codedriver.framework.datawarehouse.enums.FieldInputType;
import codedriver.framework.datawarehouse.enums.FieldType;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class DataSourceFieldVo extends BasePageVo {
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
    @EntityField(name = "是否作为条件", type = ApiParamType.INTEGER)
    private int isCondition;
    @EntityField(name = "条件输入控件配置", type = ApiParamType.JSONOBJECT)
    private JSONObject config;
    @JSONField(serialize = false)
    private String configStr;
    @JSONField(serialize = false)
    private Object value;//值,作为条件

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSourceFieldVo that = (DataSourceFieldVo) o;
        return name.equals(that.name) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    public DataSourceFieldVo() {

    }

    public DataSourceFieldVo(DataSourceFieldVo _dataSourceFieldVo) {
        this.name = _dataSourceFieldVo.getName();
        this.label = _dataSourceFieldVo.getLabel();
        this.type = _dataSourceFieldVo.getType();
        this.id = _dataSourceFieldVo.getId();
        this.isKey = _dataSourceFieldVo.getIsKey();
    }

    public DataSourceFieldVo(String name, String label, String type, Integer isKey) {
        this.name = name;
        this.label = label;
        this.type = type;
        this.isKey = isKey;
    }

    public int getIsCondition() {
        return isCondition;
    }

    @JSONField(serialize = false)
    public String getSqlConditionExpression() {
        if (StringUtils.isNotBlank(this.inputType)) {
            IDatasourceConditionHandler handler = DatasourceConditionHandlerFactory.getHandler(this.inputType);
            if (handler != null) {
                return handler.getExpression(this.id, this.value);
            }
        }
        return null;
    }

    public void setIsCondition(int isCondition) {
        this.isCondition = isCondition;
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
        if (config == null && StringUtils.isNotBlank(configStr)) {
            try {
                config = JSONObject.parseObject(configStr);
            } catch (Exception ignored) {

            }
        }
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public String getConfigStr() {
        if (config != null) {
            configStr = config.toString();
        }
        return configStr;
    }

    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }
}
