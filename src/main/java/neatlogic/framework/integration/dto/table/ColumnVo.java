/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

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

package neatlogic.framework.integration.dto.table;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.Expression;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dto.ExpressionVo;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: neatlogic
 * @description:
 * @create: 2020-03-27 18:03
 **/
public class ColumnVo extends BasePageVo {
    @EntityField( name = "属性uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField( name = "属性名", type = ApiParamType.STRING)
    private String name;
    @EntityField( name = "类型", type = ApiParamType.STRING)
    private String type = "input";
    @EntityField( name = "是否必填", type = ApiParamType.INTEGER)
    private Integer isRequired;
    @EntityField( name = "排序", type = ApiParamType.INTEGER)
    private Integer sort;
    @EntityField( name = "配置信息", type = ApiParamType.STRING)
    private String config;
    @EntityField(name = "是否能搜索", type = ApiParamType.INTEGER)
	private Integer isSearchable = 1;
    @EntityField(name = "是否搜索", type = ApiParamType.INTEGER)
	private Integer isSearch;
    @EntityField(name = "是否能搜索", type = ApiParamType.INTEGER)
    private Integer primaryKey;
    @EntityField( name = "表达式列表", type = ApiParamType.JSONARRAY)
    private List<ExpressionVo> expressionList;
    @EntityField( name = "默认表达式", type = ApiParamType.JSONOBJECT)
    private ExpressionVo defaultExpression;
    @EntityField(name = "是否在PC端显示", type = ApiParamType.BOOLEAN)
    private Boolean isPC;
    @EntityField(name = "是否在移动端显示", type = ApiParamType.BOOLEAN)
    private Boolean isMobile;
    @EntityField( name = "链接跳转属性", type = ApiParamType.STRING)
    private String urlAttributeValue;

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

	public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getIsSearchable() {
		return isSearchable;
	}

	public void setIsSearchable(Integer isSearchable) {
		this.isSearchable = isSearchable;
	}

    public Integer getIsSearch() {
        return isSearch;
    }

    public void setIsSearch(Integer isSearch) {
        this.isSearch = isSearch;
    }

    public Integer getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(Integer primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<ExpressionVo> getExpressionList() {
        if(expressionList == null) {
            expressionList = new ArrayList<>();
            expressionList.add(new ExpressionVo(Expression.EQUAL));
        }
        return expressionList;
    }

    public void setExpressionList(List<ExpressionVo> expressionList) {
        this.expressionList = expressionList;
    }

    public ExpressionVo getDefaultExpression() {
        if(defaultExpression == null) {
            defaultExpression = new ExpressionVo(Expression.EQUAL);
        }
        return defaultExpression;
    }

    public void setDefaultExpression(ExpressionVo defaultExpression) {
        this.defaultExpression = defaultExpression;
    }

    public Boolean getIsPC() {
        return isPC;
    }

    public void setIsPC(Boolean isPC) {
        this.isPC = isPC;
    }

    public Boolean getIsMobile() {
        return isMobile;
    }

    public void setIsMobile(Boolean isMobile) {
        this.isMobile = isMobile;
    }

    public String getUrlAttributeValue() {
        return urlAttributeValue;
    }

    public void setUrlAttributeValue(String urlAttributeValue) {
        this.urlAttributeValue = urlAttributeValue;
    }
}
