/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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
