/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.dto.table;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.constvalue.Expression;
import codedriver.framework.common.dto.BasePageVo;
import codedriver.framework.dto.ExpressionVo;
import codedriver.framework.matrix.constvalue.MatrixAttributeType;
import codedriver.framework.restful.annotation.EntityField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-03-27 18:03
 **/
public class ColumnVo extends BasePageVo {
    @EntityField( name = "属性uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField( name = "属性名", type = ApiParamType.STRING)
    private String name;
    @EntityField( name = "类型", type = ApiParamType.STRING)
    private String type;
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
}
