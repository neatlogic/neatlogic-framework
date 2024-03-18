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
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DataSourceDataVo extends BasePageVo {
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "nfdd.datasourcedatavo.entityfield.name.datasourceid", type = ApiParamType.LONG)
    private Long dataSourceId;
    @EntityField(name = "nfdd.datasourcevo.entityfield.name.fieldlist", type = ApiParamType.JSONARRAY)
    private List<DataSourceFieldVo> fieldList;
    @EntityField(name = "nfdd.datasourcedatavo.entityfield.name.expiremin", type = ApiParamType.INTEGER)
    private Integer expireMinute;
    @EntityField(name = "nfdd.datasourcedatavo.entityfield.name.inserttime", type = ApiParamType.LONG)
    private Date insertTime;
    @EntityField(name = "nfd.licensevo.entityfield.name.expirationdate", type = ApiParamType.LONG)
    private Date expireTime;
    @EntityField(name = "nfdd.datasourcedatavo.entityfield.name.conditionlist", type = ApiParamType.JSONARRAY)
    private List<DataSourceFieldVo> conditionList;
    @EntityField(name = "common.isexpired", type = ApiParamType.INTEGER)
    private Integer isExpired = 0;
    @JSONField(serialize = false)
    private List<DataSourceFieldSortVo> sortList;//排序设置
    @JSONField(serialize = false)
    private int limit;//限制返回行数，0代表不限制

    public void addField(DataSourceFieldVo fieldVo) {
        if (fieldList == null) {
            fieldList = new ArrayList<>();
        }
        fieldList.add(fieldVo);
    }

    public DataSourceFieldVo getFieldById(Long id) {
        if (CollectionUtils.isNotEmpty(fieldList)) {
            Optional<DataSourceFieldVo> op = fieldList.stream().filter(d -> d.getId().equals(id)).findFirst();
            if (op.isPresent()) {
                return op.get();
            }
        }
        return null;
    }


    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<DataSourceFieldSortVo> getSortList() {
        return sortList;
    }

    public void setSortList(List<DataSourceFieldSortVo> sortList) {
        this.sortList = sortList;
    }

    public DataSourceDataVo() {

    }

    public DataSourceDataVo(Long dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    public String getTableName() {
        return TenantContext.get().getDataDbName() + ".`datasource_" + this.dataSourceId + "`";
    }

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public boolean containField(Long fieldId) {
        if (CollectionUtils.isNotEmpty(fieldList)) {
            return fieldList.stream().anyMatch(d -> d.getId().equals(fieldId));
        }
        return false;
    }

    public Integer getExpireMinute() {
        return expireMinute;
    }

    public Integer getIsExpired() {
        return isExpired;
    }

    public void setIsExpired(Integer isExpired) {
        this.isExpired = isExpired;
    }

    public void setExpireMinute(Integer expireMinute) {
        this.expireMinute = expireMinute;
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

    public List<DataSourceFieldVo> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<DataSourceFieldVo> fieldList) {
        this.fieldList = fieldList;
    }


    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public List<DataSourceFieldVo> getConditionList() {
        return conditionList;
    }

    public void setConditionList(List<DataSourceFieldVo> conditionList) {
        this.conditionList = conditionList;
    }
}
