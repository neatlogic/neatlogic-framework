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
