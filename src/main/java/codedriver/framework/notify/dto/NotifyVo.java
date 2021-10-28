package codedriver.framework.notify.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import codedriver.framework.file.dto.FileVo;
import codedriver.framework.message.core.IMessageHandler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.notify.core.INotifyTriggerType;
import codedriver.framework.util.FreemarkerUtil;

public class NotifyVo {
    private INotifyTriggerType triggerType;
    private String notifyPolicyHandler;
    private String title;
    private String content;
    private List<FileVo> fileList;
    private String fromUser;
    private String fromUserEmail;
    private JSONObject data;
    private MessageHandlerAndRecipientVo messageHandlerAndRecipientVo;
    private List<String> exceptionNotifyUserUuidList;
    private StringBuilder errorBuilder;
    private String exception;// 记录通知发生异常时的异常信息
    private Date fcd;

    private NotifyVo(Builder builder) {
        this.triggerType = builder.triggerType;
        this.notifyPolicyHandler = builder.notifyPolicyHandler;
        this.data = builder.data;
        this.messageHandlerAndRecipientVo = new MessageHandlerAndRecipientVo(builder);
        this.exceptionNotifyUserUuidList = builder.exceptionNotifyUserUuidList;
        this.fcd = builder.fcd;
        this.fileList = builder.fileList;
        try {
            title = FreemarkerUtil.transform(builder.data, builder.templateTitle);
        } catch (Exception e) {
            this.appendError(e.getMessage());
        }
        try {
            content = FreemarkerUtil.transform(builder.data, builder.templateContent);
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

    public List<String> getExceptionNotifyUserUuidList() {
        return exceptionNotifyUserUuidList;
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

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
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

    public static class Builder {
        // 可选参数
        private String templateContent;
        private String templateTitle;
        private JSONObject data = new JSONObject();
        private List<FileVo> fileList = new ArrayList<>();
        private List<String> toUserUuidList = new ArrayList<>();
        private List<String> toTeamUuidList = new ArrayList<>();
        private List<String> toRoleUuidList = new ArrayList<>();

        private List<String> exceptionNotifyUserUuidList = new ArrayList<>();

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

        public Builder setExceptionNotifyUserUuidList(List<String> exceptionNotifyUserUuidList) {
            this.exceptionNotifyUserUuidList = exceptionNotifyUserUuidList;
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
