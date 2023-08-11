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
