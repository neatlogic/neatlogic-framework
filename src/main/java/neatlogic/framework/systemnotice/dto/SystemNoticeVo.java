package neatlogic.framework.systemnotice.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.dto.BaseEditorVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.I18n;
import neatlogic.framework.util.SnowflakeUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Title: SystemNoticeVo
 * @Package: neatlogic.framework.systemnotice.dto
 * @Description: 系统公告VO
 * @Author: laiwt
 * @Date: 2021/1/13 17:40
 **/
public class SystemNoticeVo extends BaseEditorVo {

    public enum Status {
        NOTISSUED("not_issued", new I18n("nfsn.systemnoticevo.status.notissued"), ""),
        ISSUED("issued", new I18n("nfsn.systemnoticevo.status.issued"), "#15bf81"),
        STOPPED("stopped", new I18n("nfsn.systemnoticevo.status.stopped"), "#ff8484");
        private String value;
        private I18n text;
        private String color;

        public String getValue() {
            return value;
        }

        public String getText() {
            return text.toString();
        }

        public String getColor() {
            return color;
        }

        private Status(String value, I18n text, String color) {
            this.value = value;
            this.text = text;
            this.color = color;
        }

        public static JSONObject getStatus(String value) {
            for (Status status : Status.values()) {
                if (status.getValue().equals(value)) {
                    JSONObject obj = new JSONObject();
                    obj.put("value", value);
                    obj.put("text", status.getText());
                    obj.put("color", status.getColor());
                    return obj;
                }
            }
            return null;
        }
    }

    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.id", type = ApiParamType.LONG)
    private Long id;
    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.title", type = ApiParamType.STRING)
    private String title;
    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.content", type = ApiParamType.STRING)
    private String content;
    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.starttime", type = ApiParamType.LONG)
    private Date startTime;
    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.endtime", type = ApiParamType.LONG)
    private Date endTime;
    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.status", type = ApiParamType.STRING)
    private String status;
    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.popup", type = ApiParamType.STRING)
    private String popUp;
    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.ignoreread", type = ApiParamType.INTEGER)
    private Integer ignoreRead;

    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.recipientvolist", type = ApiParamType.JSONARRAY)
    @JSONField(serialize = false)
    private List<SystemNoticeRecipientVo> recipientVoList;

    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.recipientlist", type = ApiParamType.JSONARRAY)
    private List<String> recipientList;

    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.recipientobjlist", type = ApiParamType.JSONARRAY)
    private List<Object> recipientObjList;

    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.statusvo", type = ApiParamType.JSONOBJECT)
    private JSONObject statusVo;

    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.issuetime", type = ApiParamType.LONG)
    private Date issueTime;

    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.imglist", type = ApiParamType.JSONARRAY)
    private List<String> imgList;

    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.isread", type = ApiParamType.INTEGER)
    private Integer isRead;

    @EntityField(name = "nfsn.systemnoticevo.entityfield.name.direction", type = ApiParamType.STRING)
    @JSONField(serialize = false)
    private String direction;

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

    public List<SystemNoticeRecipientVo> getRecipientVoList() {
        return recipientVoList;
    }

    public void setRecipientVoList(List<SystemNoticeRecipientVo> recipientVoList) {
        this.recipientVoList = recipientVoList;
    }

    public void setRecipientList(List<String> recipientList) {
        this.recipientList = recipientList;
    }

    public List<String> getRecipientList() {
        if (CollectionUtils.isEmpty(recipientList) && CollectionUtils.isNotEmpty(recipientVoList)) {
            recipientList = new ArrayList<>();
            for (SystemNoticeRecipientVo vo : recipientVoList) {
                GroupSearch groupSearch = GroupSearch.getGroupSearch(vo.getType());
                if (groupSearch != null) {
                    recipientList.add(groupSearch.getValuePlugin() + vo.getUuid());
                }
            }
        }
        return recipientList;
    }

    public List<Object> getRecipientObjList() {
        return recipientObjList;
    }

    public void setRecipientObjList(List<Object> recipientObjList) {
        this.recipientObjList = recipientObjList;
    }

    public JSONObject getStatusVo() {
        return statusVo;
    }

    public void setStatusVo(JSONObject statusVo) {
        this.statusVo = statusVo;
    }

    public Date getIssueTime() {
        return issueTime;
    }

    public void setIssueTime(Date issueTime) {
        this.issueTime = issueTime;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
