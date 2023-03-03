package neatlogic.framework.systemnotice.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.EntityField;

/**
 * @Title: SystemNoticeRecipientVo
 * @Package: neatlogic.framework.systemnotice.dto
 * @Description: 系统公告通知对象VO
 * @Author: laiwt
 * @Date: 2021/1/13 17:40
 **/
public class SystemNoticeRecipientVo{
    @EntityField(name = "公告id", type = ApiParamType.LONG)
    private Long systemNoticeId;
    @EntityField(name = "通知对象uuid", type = ApiParamType.STRING)
    private String uuid;
    @EntityField(name = "类型", type = ApiParamType.STRING)
    private String type;

    public Long getSystemNoticeId() {
        return systemNoticeId;
    }

    public void setSystemNoticeId(Long systemNoticeId) {
        this.systemNoticeId = systemNoticeId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
