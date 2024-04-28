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

package neatlogic.framework.dto.region;

import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RegionVo extends BasePageVo{
    public final static Long ROOT_ID = 0L;
    public final static Long ROOT_PARENTID = -1L;
    @EntityField(name = "id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "名称", type = ApiParamType.STRING)
    private String name;
    @EntityField(name = "父级id", type = ApiParamType.LONG)
    private Long parentId;
    @EntityField(name = "服务窗口uuid", type = ApiParamType.LONG)
    private String workTimeUuid;
    @EntityField(name = "是否启用，0：禁用，1：启用", type = ApiParamType.INTEGER)
    private Integer isActive;
    @EntityField(name = "左编码", type = ApiParamType.INTEGER)
    private Integer lft;
    @EntityField(name = "右编码", type = ApiParamType.INTEGER)
    private Integer rht;
    @EntityField(name = "子节点数", type = ApiParamType.INTEGER)
    private int childrenCount = 0;
    @EntityField(name = "地域", type = ApiParamType.JSONARRAY)
    private List<RegionVo> children = new ArrayList<>();

    @JSONField(serialize=false)
    private RegionVo parent;

    @JSONField(serialize=false)
    private List<Long> idList;

    public Long getId() {
        if(id == null){
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

    public Long getParentId() {
        if(parentId == null){ //兼容前端tree组件无法给parentId赋值为0的问题
            parentId = 0L;
        }
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getWorkTimeUuid() {
        return workTimeUuid;
    }

    public void setWorkTimeUuid(String workTimeUuid) {
        this.workTimeUuid = workTimeUuid;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
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

    public int getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(int childrenCount) {
        this.childrenCount = childrenCount;
    }

    public boolean addChildRegion(RegionVo regionVo) {
        if(children.contains(regionVo)) {
            return false;
        }
        childrenCount++;
        return children.add(regionVo);
    }

    public boolean removeChildCatalog(RegionVo regionVo) {
        if(CollectionUtils.isNotEmpty(children)) {
            Iterator<RegionVo> iterator = children.iterator();
            while(iterator.hasNext()) {
                if(iterator.next().equals(regionVo)) {
                    iterator.remove();
                    childrenCount--;
                    return true;
                }
            }
        }
        return false;
    }

    public RegionVo getParent() {
        return parent;
    }

    public void setParent(RegionVo parent) {
        this.parent = parent;
        parent.addChildRegion(this);
    }

    public List<RegionVo> getChildren() {
        return children;
    }

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }
}
