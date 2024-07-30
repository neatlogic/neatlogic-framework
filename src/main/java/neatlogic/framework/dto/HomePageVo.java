/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.dto;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;

import java.util.List;

public class HomePageVo {
    @EntityField(name = "common.id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "common.name", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "common.isactive", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "common.sort", type = ApiParamType.INTEGER)
    private Integer sort;
    @EntityField(name = "common.config", type = ApiParamType.STRING)
    private JSONObject config;

    @JSONField(serialize = false)
    private String configStr;
    @EntityField(name = "common.authoritylist", type = ApiParamType.JSONARRAY)
    private List<String> authorityList;
    @EntityField(name = "common.authoritylist", type = ApiParamType.JSONARRAY)
    private JSONArray authorityVoList;

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getSort() {
        if (sort == null) {
            sort = 0;
        }
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public JSONObject getConfig() {
        if (config == null && configStr != null) {
            config = JSONObject.parseObject(configStr);
        }
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    public String getConfigStr() {
        if (configStr == null && config != null) {
            configStr = config.toJSONString();
        }
        return configStr;
    }

    public void setConfigStr(String configStr) {
        this.configStr = configStr;
    }

    public List<String> getAuthorityList() {
        return authorityList;
    }

    public void setAuthorityList(List<String> authorityList) {
        this.authorityList = authorityList;
    }

    public JSONArray getAuthorityVoList() {
        return authorityVoList;
    }

    public void setAuthorityVoList(JSONArray authorityVoList) {
        this.authorityVoList = authorityVoList;
    }
}
