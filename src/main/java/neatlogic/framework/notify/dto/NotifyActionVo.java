package neatlogic.framework.notify.dto;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

public class NotifyActionVo {

    private Long templateId;
    private String templateName;
    private String notifyHandler;
    private String notifyHandlerName;
    private List<String> receiverList;
    private JSONArray receiverObjList;//用作策略详情页的触发类型卡片展示

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getNotifyHandler() {
        return notifyHandler;
    }

    public void setNotifyHandler(String notifyHandler) {
        this.notifyHandler = notifyHandler;
    }

    public String getNotifyHandlerName() {
        return notifyHandlerName;
    }

    public void setNotifyHandlerName(String notifyHandlerName) {
        this.notifyHandlerName = notifyHandlerName;
    }

    public List<String> getReceiverList() {
        return receiverList;
    }

    public void setReceiverList(List<String> receiverList) {
        this.receiverList = receiverList;
    }

    public JSONArray getReceiverObjList() {
        return receiverObjList;
    }

    public void setReceiverObjList(JSONArray receiverObjList) {
        this.receiverObjList = receiverObjList;
    }
}
