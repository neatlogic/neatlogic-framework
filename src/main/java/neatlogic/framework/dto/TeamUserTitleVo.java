/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvzk
 * @since 2021/7/28 16:21
 **/
public class TeamUserTitleVo implements Serializable {
    private List<UserVo> userVoList;
    private List<String> userList;
    private String teamUuid;
    private String title;
    private Long titleId;
    @EntityField(name = "头衔排序", type = ApiParamType.STRING)
    private Integer titleSort;

    public List<UserVo> getUserVoList() {
        return userVoList;
    }

    public void setUserVoList(List<UserVo> userVoList) {
        this.userVoList = userVoList;
    }

    public List<String> getUserList() {
        if (CollectionUtils.isNotEmpty(userVoList)) {
            userList = userVoList.stream().map(UserVo::getUuid).collect(Collectors.toList());
        }
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public String getTeamUuid() {
        return teamUuid;
    }

    public void setTeamUuid(String teamUuid) {
        this.teamUuid = teamUuid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTitleId() {
        return titleId;
    }

    public void setTitleId(Long titleId) {
        this.titleId = titleId;
    }

    public Integer getTitleSort() {
        return titleSort;
    }

    public void setTitleSort(Integer titleSort) {
        this.titleSort = titleSort;
    }
}
