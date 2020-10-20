package codedriver.framework.dto;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import codedriver.framework.common.constvalue.TeamUserTitle;

public class TeamUserVo {
	
	private String teamUuid;
	private String teamName;
	private String userUuid;
	private String userName;
	private String userId;
	private String userInfo;
	private String userAvatar;
	private Integer userVipLevel;
	private String title;
	private String titleText;
	public TeamUserVo() {
	}

	public TeamUserVo(String teamUuid) {
		this.teamUuid = teamUuid;
	}

	public TeamUserVo(String teamUuid, String userUuid) {
		this.teamUuid = teamUuid;
		this.userUuid = userUuid;
	}

	public TeamUserVo(String teamUuid, String userUuid, String title) {
		this.teamUuid = teamUuid;
		this.userUuid = userUuid;
		this.title = title;
	}

	public String getTeamUuid() {
		return teamUuid;
	}
	public void setTeamUuid(String teamUuid) {
		this.teamUuid = teamUuid;
	}
	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleText() {
		if(StringUtils.isBlank(titleText) && StringUtils.isNotBlank(title)) {
			titleText = TeamUserTitle.getText(title);
		}
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public String getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(String userInfo) {
		this.userInfo = userInfo;
	}

	public String getUserAvatar() {
		if (StringUtils.isBlank(userAvatar) && StringUtils.isNotBlank(userInfo)) {
			JSONObject jsonObject = JSONObject.parseObject(userInfo);
			userAvatar = jsonObject.getString("avatar");
		}
		return userAvatar;
	}

	public Integer getUserVipLevel() {
		return userVipLevel;
	}

	public void setUserVipLevel(Integer userVipLevel) {
		this.userVipLevel = userVipLevel;
	}
}
