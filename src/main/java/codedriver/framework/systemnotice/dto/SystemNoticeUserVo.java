package codedriver.framework.systemnotice.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.restful.annotation.EntityField;

/**
 * @Title: SystemNoticeUserVo
 * @Package: codedriver.framework.systemnotice.dto
 * @Description: 系统公告通知用户VO
 * @Author: laiwt
 * @Date: 2021/1/13 17:40
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class SystemNoticeUserVo {
    @EntityField(name = "公告id", type = ApiParamType.LONG)
    private Long systemNoticeId;
    @EntityField(name = "用户uuid", type = ApiParamType.STRING)
    private String userUuid;
    @EntityField(name = "是否已读(1:已读;0:未读)", type = ApiParamType.INTEGER)
    private Integer isRead;

    public Long getSystemNoticeId() {
        return systemNoticeId;
    }

    public void setSystemNoticeId(Long systemNoticeId) {
        this.systemNoticeId = systemNoticeId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }
}
