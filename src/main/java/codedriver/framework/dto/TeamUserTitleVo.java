/*
 * Copyright (c)  2021 TechSure Co.,Ltd.  All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.dto;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lvzk
 * @since 2021/7/28 16:21
 **/
public class TeamUserTitleVo {
    private List<UserVo> userVoList;
    private List<String> userList;
    private String teamUuid;
    private String title;

    public List<UserVo> getUserVoList() {
        return userVoList;
    }

    public void setUserVoList(List<UserVo> userVoList) {
        this.userVoList = userVoList;
    }

    public List<String> getUserList() {
        if(CollectionUtils.isNotEmpty(userVoList)){
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
}
