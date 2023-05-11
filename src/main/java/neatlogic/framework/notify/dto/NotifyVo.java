package neatlogic.framework.notify.dto;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.file.dto.FileVo;
import neatlogic.framework.message.core.IMessageHandler;
import neatlogic.framework.notify.core.INotifyTriggerType;
import neatlogic.framework.util.NotifyFreemarkerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NotifyVo {
    /**
     * 触发点
     */
    private INotifyTriggerType triggerType;
    /**
     * 通知策略处理器
     */
    private String notifyPolicyHandler;
    /**
     * 邮件标题
     */
    private String title;
    /**
     * 邮件标题模板
     */
    private String templateTitle;
    /**
     * 邮件内容
     */
    private String content;
    /**
     * 邮件内容模板
     */
    private String templateContent;
    /**
     * 附件列表
     */
    private List<FileVo> fileList;
    /**
     * 发送者
     */
    private String fromUser;
    /**
     * 发送者邮箱地址
     */
    private String fromUserEmail;
    /**
     * 用于FreeMarker替换的变量数据
     */
    private JSONObject data;
    /**
     * 消息处理器和接收者信息
     */
    private MessageHandlerAndRecipientVo messageHandlerAndRecipientVo;
    /**
     * freeMarker模板替换时出现的异常信息
     */
    private StringBuilder errorBuilder;
    /**
     * 通知发生异常时是否需要发送通知
     */
    private Integer isSendExceptionNotify = 1;
    /**
     * 发送时间
     */
    private Date fcd;
    /**
     * 实际接收对象列表
     */
    private List<String> actualRecipientList;

    /**
     * 发起对象，用于异常通知
     */
    private Object callerData;

    /**
     * 发起对象通知策略类型，用于异常通知
     */
    private Class<? extends IMessageHandler> callerMessageHandlerClass;

    /**
     * 发起对象通知策略，用于异常通知
     */
    private NotifyPolicyVo callerNotifyPolicyVo;

    private NotifyVo(Builder builder) {
        this.triggerType = builder.triggerType;
        this.notifyPolicyHandler = builder.notifyPolicyHandler;
        this.data = builder.data;
        this.messageHandlerAndRecipientVo = new MessageHandlerAndRecipientVo(builder);
        this.fcd = builder.fcd;
        this.fileList = builder.fileList;
        this.templateTitle = builder.templateTitle;
        this.templateContent = builder.templateContent;
        try {
            title = NotifyFreemarkerUtil.transform(builder.data, builder.templateTitle);
        } catch (Exception e) {
            this.appendError(e.getMessage());
        }
        try {
            content = NotifyFreemarkerUtil.transform(builder.data, builder.templateContent);
        } catch (Exception e) {
            this.appendError(e.getMessage());
        }
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTemplateTitle() {
        return templateTitle;
    }

    public String getTemplateContent() {
        return templateContent;
    }

    public List<FileVo> getFileList() {
        return fileList;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public JSONObject getData() {
        return data;
    }

    public String getFromUserEmail() {
        return fromUserEmail;
    }

    public void setFromUserEmail(String fromUserEmail) {
        this.fromUserEmail = fromUserEmail;
    }

    public List<String> getToUserUuidList() {
        return messageHandlerAndRecipientVo.toUserUuidList;
    }

    public List<String> getToTeamUuidList() {
        return messageHandlerAndRecipientVo.toTeamUuidList;
    }

    public List<String> getToRoleUuidList() {
        return messageHandlerAndRecipientVo.toRoleUuidList;
    }

    public String getError() {
        if (errorBuilder != null) {
            return errorBuilder.toString().trim();
        }
        return null;
    }

    public void appendError(String errorInfo) {
        if (StringUtils.isNotBlank(errorInfo)) {
            if (errorBuilder == null) {
                errorBuilder = new StringBuilder();
            }
            errorBuilder.append(errorInfo);
        }
    }

    public Integer getIsSendExceptionNotify() {
        return isSendExceptionNotify;
    }

    public void setIsSendExceptionNotify(Integer isSendExceptionNotify) {
        this.isSendExceptionNotify = isSendExceptionNotify;
    }

    public INotifyTriggerType getTriggerType() {
        return this.triggerType;
    }

    public String getNotifyPolicyHandler() {
        return notifyPolicyHandler;
    }

    public Class<? extends IMessageHandler> getMessageHandlerClass() {
        return messageHandlerAndRecipientVo.messageHandlerClass;
    }

    public MessageHandlerAndRecipientVo getMessageHandlerAndRecipientVo() {
        return messageHandlerAndRecipientVo;
    }

    public void setMessageHandlerAndRecipientVo(MessageHandlerAndRecipientVo messageHandlerAndRecipientVo) {
        this.messageHandlerAndRecipientVo = messageHandlerAndRecipientVo;
    }

    public Date getFcd() {
        return fcd;
    }

    public List<String> getActualRecipientList() {
        return actualRecipientList;
    }

    public void setActualRecipientList(List<String> actualRecipientList) {
        this.actualRecipientList = actualRecipientList;
    }

    public Object getCallerData() {
        return callerData;
    }

    public void setCallerData(Object callerData) {
        this.callerData = callerData;
    }

    public Class<? extends IMessageHandler> getCallerMessageHandlerClass() {
        return callerMessageHandlerClass;
    }

    public void setCallerMessageHandlerClass(Class<? extends IMessageHandler> callerMessageHandlerClass) {
        this.callerMessageHandlerClass = callerMessageHandlerClass;
    }

    public NotifyPolicyVo getCallerNotifyPolicyVo() {
        return callerNotifyPolicyVo;
    }

    public void setCallerNotifyPolicyVo(NotifyPolicyVo callerNotifyPolicyVo) {
        this.callerNotifyPolicyVo = callerNotifyPolicyVo;
    }

    public static class Builder {
        // 可选参数
        private String templateContent;
        private String templateTitle;
        private JSONObject data = new JSONObject();
        private List<FileVo> fileList = new ArrayList<>();
        private List<String> toUserUuidList = new ArrayList<>();
        private List<String> toTeamUuidList = new ArrayList<>();
        private List<String> toRoleUuidList = new ArrayList<>();

        private INotifyTriggerType triggerType;
        private Class<? extends IMessageHandler> messageHandlerClass;
        private String notifyPolicyHandler;
        private Date fcd = new Date();

        public Builder() {

        }

        public Builder(INotifyTriggerType _triggerType) {
            this.triggerType = _triggerType;
        }

        public Builder(INotifyTriggerType _triggerType, Class<? extends IMessageHandler> _messageHandlerClass) {
            this.triggerType = _triggerType;
            this.messageHandlerClass = _messageHandlerClass;
        }

        public Builder(INotifyTriggerType _triggerType, Class<? extends IMessageHandler> _messageHandlerClass, String _notifyPolicyHandler) {
            this.triggerType = _triggerType;
            this.messageHandlerClass = _messageHandlerClass;
            this.notifyPolicyHandler = _notifyPolicyHandler;
        }

        public INotifyTriggerType getTriggerType() {
            return triggerType;
        }

        public String getNotifyPolicyHandler() {
            return notifyPolicyHandler;
        }

        public Builder withContentTemplate(String contentTemplate) {
            templateContent = contentTemplate;
            return this;
        }

        public Builder withTitleTemplate(String titleTemplate) {
            templateTitle = titleTemplate;
            return this;
        }

        public Builder addData(String key, Object value) {
            data.put(key, value);
            return this;
        }

        public Builder addAllData(JSONObject dataObj) {
            data.putAll(dataObj);
            return this;
        }

        public NotifyVo build() {
            return new NotifyVo(this);
        }

        public Builder addUserUuid(String userUuid) {
            if (!toUserUuidList.contains(userUuid)) {
                toUserUuidList.add(userUuid);
            }
            return this;
        }

        public Builder addTeamUuid(String teamUuid) {
            if (!toTeamUuidList.contains(teamUuid)) {
                toTeamUuidList.add(teamUuid);
            }
            return this;
        }

        public Builder addRoleUuid(String roleUuid) {
            if (!toRoleUuidList.contains(roleUuid)) {
                toRoleUuidList.add(roleUuid);
            }
            return this;
        }

        public Builder addFileList(List<FileVo> fileList) {
            if (CollectionUtils.isNotEmpty(fileList)) {
                this.fileList.addAll(fileList);
            }
            return this;
        }
    }

    public static class MessageHandlerAndRecipientVo {

        private Class<? extends IMessageHandler> messageHandlerClass;
        private List<String> toUserUuidList;
        private List<String> toTeamUuidList;
        private List<String> toRoleUuidList;

        public MessageHandlerAndRecipientVo(Builder builder) {
            this.messageHandlerClass = builder.messageHandlerClass;
            this.toUserUuidList = builder.toUserUuidList;
            this.toTeamUuidList = builder.toTeamUuidList;
            this.toRoleUuidList = builder.toRoleUuidList;
        }

        public Class<? extends IMessageHandler> getMessageHandlerClass() {
            return messageHandlerClass;
        }

        public List<String> getToUserUuidList() {
            return toUserUuidList;
        }

        public List<String> getToTeamUuidList() {
            return toTeamUuidList;
        }

        public List<String> getToRoleUuidList() {
            return toRoleUuidList;
        }



        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MessageHandlerAndRecipientVo that = (MessageHandlerAndRecipientVo) o;
            return messageHandlerClass.equals(that.messageHandlerClass) && toUserUuidList.equals(that.toUserUuidList) && toTeamUuidList.equals(that.toTeamUuidList) && toRoleUuidList.equals(that.toRoleUuidList);
        }

        @Override
        public int hashCode() {
            return Objects.hash(messageHandlerClass, toUserUuidList, toTeamUuidList, toRoleUuidList);
        }
    }
}
