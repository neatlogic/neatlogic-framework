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

package neatlogic.framework.datawarehouse.dto;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DataSourceDataVo extends BasePageVo {
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "数据源id", type = ApiParamType.LONG)
    private Long dataSourceId;
    @EntityField(name = "字段列表", type = ApiParamType.JSONARRAY)
    private List<DataSourceFieldVo> fieldList;
    @EntityField(name = "有效时间，单位分钟", type = ApiParamType.INTEGER)
    private Integer expireMinute;
    @EntityField(name = "添加日期", type = ApiParamType.LONG)
    private Date insertTime;
    @EntityField(name = "过期日期", type = ApiParamType.LONG)
    private Date expireTime;
    @EntityField(name = "条件列表", type = ApiParamType.JSONARRAY)
    private List<DataSourceFieldVo> conditionList;
    @EntityField(name = "是否过期", type = ApiParamType.INTEGER)
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
