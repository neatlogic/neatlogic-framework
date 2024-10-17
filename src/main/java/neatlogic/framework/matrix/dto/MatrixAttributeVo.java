package neatlogic.framework.matrix.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dto.ExpressionVo;
import neatlogic.framework.matrix.constvalue.MatrixAttributeType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-03-27 18:03
 **/
public class MatrixAttributeVo extends BasePageVo {
    @EntityField( name = "矩阵uuid", type = ApiParamType.STRING)
    private String matrixUuid;
    @EntityField( name = "属性uuid", type = ApiParamType.STRING)
    private String uuid;

	@EntityField( name = "属性唯一标识", type = ApiParamType.STRING)
	private String uniqueIdentifier;

	@EntityField( name = "默认属性唯一标识", type = ApiParamType.STRING)
	private String defaultUniqueIdentifier;
    @EntityField( name = "属性名", type = ApiParamType.STRING)
    private String name;
    @EntityField( name = "属性label", type = ApiParamType.STRING)
    private String label;
    @EntityField( name = "类型", type = ApiParamType.STRING)
    private String type;
    @EntityField( name = "是否必填", type = ApiParamType.INTEGER)
    private Integer isRequired;
    @EntityField( name = "排序", type = ApiParamType.INTEGER)
    private Integer sort;
	@EntityField(name = "是否能删除", type = ApiParamType.INTEGER)
	private Integer isDeletable = 1;
    @EntityField( name = "配置信息", type = ApiParamType.JSONOBJECT)
    private JSONObject config;
    @EntityField( name = "配置信息", type = ApiParamType.STRING)
	@JSONField(serialize = false)
    private String configStr;
    @EntityField( name = "表达式列表", type = ApiParamType.JSONARRAY)
    private List<ExpressionVo> expressionList;
    @EntityField( name = "默认表达式", type = ApiParamType.JSONOBJECT)
    private ExpressionVo defaultExpression;
    @EntityField(name = "是否能搜索", type = ApiParamType.INTEGER)
	private Integer isSearchable = 1;
	@EntityField(name = "是否是主键", type = ApiParamType.INTEGER)
    private Integer primaryKey = 0;

    public String getMatrixUuid() {
        return matrixUuid;
    }

    public void setMatrixUuid(String matrixUuid) {
        this.matrixUuid = matrixUuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}

	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}

	public String getDefaultUniqueIdentifier() {
		return defaultUniqueIdentifier;
	}

	public void setDefaultUniqueIdentifier(String defaultUniqueIdentifier) {
		this.defaultUniqueIdentifier = defaultUniqueIdentifier;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getIsRequired() {
		return isRequired;
	}

	public void setIsRequired(Integer isRequired) {
		this.isRequired = isRequired;
	}

	public JSONObject getConfig() {
    	if (config == null && StringUtils.isNotBlank(configStr)) {
			config = JSONObject.parseObject(configStr);
		}
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

	public String getConfigStr() {
    	if (StringUtils.isBlank(configStr) && config != null) {
			configStr = config.toJSONString();
		}
		return configStr;
	}

	public void setConfigStr(String configStr) {
		this.configStr = configStr;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getIsDeletable() {
		return isDeletable;
	}

	public void setIsDeletable(Integer isDeletable) {
		this.isDeletable = isDeletable;
	}

	public List<ExpressionVo> getExpressionList() {
		if(expressionList == null && StringUtils.isNotBlank(type)) {
			List<Expression> expressionEnumList = MatrixAttributeType.getExpressionList(type);
			if(CollectionUtils.isNotEmpty(expressionEnumList)) {
				expressionList = new ArrayList<>();
				for(Expression expression : expressionEnumList) {
					expressionList.add(new ExpressionVo(expression));
				}
			}
		}
		return expressionList;
	}

	public void setExpressionList(List<ExpressionVo> expressionList) {
		this.expressionList = expressionList;
	}

	public ExpressionVo getDefaultExpression() {
		if(defaultExpression == null && StringUtils.isNotBlank(type)) {
			Expression expressionEnum = MatrixAttributeType.getExpression(type);
			if(expressionEnum != null) {
				defaultExpression = new ExpressionVo(expressionEnum);
			}
		}
		return defaultExpression;
	}

	public void setDefaultExpression(ExpressionVo defaultExpression) {
		this.defaultExpression = defaultExpression;
	}

	public Integer getIsSearchable() {
		return isSearchable;
	}

	public void setIsSearchable(Integer isSearchable) {
		this.isSearchable = isSearchable;
	}

	public Integer getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(Integer primaryKey) {
		this.primaryKey = primaryKey;
	}
}
