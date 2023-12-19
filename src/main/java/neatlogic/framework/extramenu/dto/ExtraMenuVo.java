/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package neatlogic.framework.extramenu.dto;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.dto.AuthorityVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class ExtraMenuVo {
    public static final Long ROOT_PARENTID = -1L;
    public static final Long ROOT_ID = 0L;
    public static final String ROOT_NAME = "root";

    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "common.name", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "common.type", type = ApiParamType.INTEGER)
    private Integer type;
    @EntityField(name = "common.isactive", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "url", type = ApiParamType.STRING)
    private String url;
    @EntityField(name = "common.description", type = ApiParamType.STRING)
    private String description;
    @EntityField(name = "common.authlist", type = ApiParamType.JSONARRAY)
    private List<String> authorityList = new ArrayList<>();
    @EntityField(name = "common.parentid", type = ApiParamType.LONG)
    private Long parentId;
    @EntityField(name = "common.lft", type = ApiParamType.INTEGER)
    private Integer lft;
    @EntityField(name = "common.rht", type = ApiParamType.INTEGER)
    private Integer rht;
    @EntityField(name = "nfdd.drorganizationvo.entityfield.childcount.name", type = ApiParamType.INTEGER)
    private Integer childCount;
    @EntityField(name = "nfdd.drorganizationvo.entityfield.children.name", type = ApiParamType.JSONARRAY)
    private List<ExtraMenuVo> children;
    @JSONField(serialize = false)
    private List<AuthorityVo> authorityVoList;
    @JSONField(serialize = false)
    private ExtraMenuVo parent;

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAuthorityList() {
        if (CollectionUtils.isEmpty(authorityList) && CollectionUtils.isNotEmpty(authorityVoList)) {
            for (AuthorityVo authorityVo : authorityVoList) {
                GroupSearch groupSearch = GroupSearch.getGroupSearch(authorityVo.getType());
                if (groupSearch != null) {
                    authorityList.add(groupSearch.getValuePlugin() + authorityVo.getUuid());
                }
            }
        }
        return authorityList;
    }

    public void setAuthorityList(List<String> authorityList) {
        this.authorityList = authorityList;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getLft() {
        return lft;
    }

    public void setLft(Integer lft) {
        this.lft = lft;
    }

    public Integer getRht() {
        return rht;
    }

    public void setRht(Integer rht) {
        this.rht = rht;
    }

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public List<ExtraMenuVo> getChildren() {
        return children;
    }

    public void setChildren(List<ExtraMenuVo> children) {
        this.children = children;
    }

    public List<AuthorityVo> getAuthorityVoList() {
        if (authorityVoList == null && CollectionUtils.isNotEmpty(authorityList)) {
            authorityVoList = new ArrayList<>();
            for (String authority : authorityList) {
                String[] split = authority.split("#");
                if (GroupSearch.getGroupSearch(split[0]) != null) {
                    AuthorityVo authorityVo = new AuthorityVo();
                    authorityVo.setType(split[0]);
                    authorityVo.setUuid(split[1]);
                    authorityVoList.add(authorityVo);
                }
            }
        }
        return authorityVoList;
    }

    public void setAuthorityVoList(List<AuthorityVo> authorityVoList) {
        this.authorityVoList = authorityVoList;
    }

    public ExtraMenuVo getParent() {
        return parent;
    }

    public void setParent(ExtraMenuVo parent) {
        if (parent != null) {
            this.parent = parent;
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(this);
        }
    }
}
