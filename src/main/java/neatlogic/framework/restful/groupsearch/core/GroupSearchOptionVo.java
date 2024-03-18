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

package neatlogic.framework.restful.groupsearch.core;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

import java.util.List;

public class GroupSearchOptionVo {

    @EntityField(name = "common.value", type = ApiParamType.STRING)
    private String value;
    @EntityField(name = "common.text", type = ApiParamType.STRING)
    private String text;
    // 用户类型特有字段
    @EntityField(name = "term.framework.pinyin", type = ApiParamType.STRING)
    private String pinyin;
    @EntityField(name = "common.group", type = ApiParamType.STRING)
    private String team;
    @EntityField(name = "common.avatar", type = ApiParamType.STRING)
    private String avatar;
    @EntityField(name = "term.framework.user.viplevel", type = ApiParamType.ENUM)
    private Integer vipLevel;
    // 分组类型特有字段
    @EntityField(name = "term.framework.team.fullpath", type = ApiParamType.STRING)
    private String fullPath;
    @EntityField(name = "term.framework.team.parentpathlist", type = ApiParamType.JSONARRAY)
    private List<String> parentPathList;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public List<String> getParentPathList() {
        return parentPathList;
    }

    public void setParentPathList(List<String> parentPathList) {
        this.parentPathList = parentPathList;
    }
}
