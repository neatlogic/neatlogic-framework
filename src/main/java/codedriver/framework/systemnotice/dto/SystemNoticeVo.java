package codedriver.framework.systemnotice.dto;

import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.dto.BaseEditorVo;
import codedriver.framework.restful.annotation.EntityField;
import codedriver.framework.util.SnowflakeUtil;

import java.util.Date;

/**
 * @Title: SystemNoticeVo
 * @Package: codedriver.framework.systemnotice.dto
 * @Description: 系统公告VO
 * @Author: laiwt
 * @Date: 2021/1/13 17:40
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class SystemNoticeVo extends BaseEditorVo {

    public enum Status{
        NOTISSUED("not_issued","未下发",""),
        ISSUED("issued","已下发",""),
        STOPPED("stopped","停用","");
        private String value;
        private String text;
        private String color;

        public String getValue() {
            return value;
        }

        public String getText() {
            return text;
        }

        public String getColor() {
            return color;
        }

        private Status(String value, String text, String color) {
            this.value = value;
            this.text = text;
            this.color = color;
        }
    }

    @EntityField(name = "公告id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "标题", type = ApiParamType.STRING)
    private String title;
    @EntityField(name = "内容", type = ApiParamType.STRING)
    private String content;
    @EntityField(name = "生效时间", type = ApiParamType.LONG)
    private Date startTime;
    @EntityField(name = "失效时间", type = ApiParamType.LONG)
    private Date endTime;
    @EntityField(name = "状态(not_issued:未下发;issued:已下发;stopped:停用)", type = ApiParamType.STRING)
    private String status;
    @EntityField(name = "弹框方式", type = ApiParamType.STRING)
    private String popUp;
    @EntityField(name = "是否忽略已读", type = ApiParamType.INTEGER)
    private Integer ignoreRead;

    public Long getId() {
        if (id == null) {
            id = SnowflakeUtil.uniqueLong();
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPopUp() {
        return popUp;
    }

    public void setPopUp(String popUp) {
        this.popUp = popUp;
    }

    public Integer getIgnoreRead() {
        return ignoreRead;
    }

    public void setIgnoreRead(Integer ignoreRead) {
        this.ignoreRead = ignoreRead;
    }
}
