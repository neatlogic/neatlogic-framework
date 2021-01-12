package codedriver.framework.notify.dto;

import java.util.ArrayList;
import java.util.List;

import codedriver.framework.message.core.IMessageHandler;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.notify.core.INotifyTriggerType;
import codedriver.framework.util.FreemarkerUtil;

public class NotifyVo {
    private INotifyTriggerType triggerType;
    private Class<? extends IMessageHandler> messageHandlerClass;
    private String title;
    private String content;
    private List<String> toUserUuidList;
    private List<String> toTeamUuidList;
    private List<String> toRoleUuidList;
    private String fromUser;
    private String fromUserEmail;
    private JSONObject data;

    private List<String> exceptionNotifyUserUuidList;
    private StringBuilder errorBuilder;

    private NotifyVo(Builder builder) {
        this.triggerType = builder.triggerType;
        this.messageHandlerClass = builder.messageHandlerClass;
        this.data = builder.data;
        this.toUserUuidList = builder.toUserUuidList;
        this.toTeamUuidList = builder.toTeamUuidList;
        this.toRoleUuidList = builder.toRoleUuidList;
        this.exceptionNotifyUserUuidList = builder.exceptionNotifyUserUuidList;
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
        return toUserUuidList;
    }

    public List<String> getToTeamUuidList() {
        return toTeamUuidList;
    }

    public List<String> getToRoleUuidList() {
        return toRoleUuidList;
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

    public INotifyTriggerType getTriggerType() {
        return this.triggerType;
    }

    public Class<? extends IMessageHandler> getMessageHandlerClass() {
        return this.messageHandlerClass;
    }

    public static class Builder {
        // 可选参数
        private String templateContent;
        private String templateTitle;
        private JSONObject data = new JSONObject();
        private List<String> toUserUuidList = new ArrayList<>();
        private List<String> toTeamUuidList = new ArrayList<>();
        private List<String> toRoleUuidList = new ArrayList<>();

        private List<String> exceptionNotifyUserUuidList = new ArrayList<>();

        private INotifyTriggerType triggerType;
        private Class<? extends IMessageHandler> messageHandlerClass;

        public Builder(INotifyTriggerType _triggerType, Class<? extends IMessageHandler> _messageHandlerClass) {
            this.triggerType = _triggerType;
            this.messageHandlerClass = _messageHandlerClass;
        }

        public INotifyTriggerType getTriggerType() {
            return triggerType;
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

        public Builder setExceptionNotifyUserUuidList(List<String> exceptionNotifyUserUuidList) {
            this.exceptionNotifyUserUuidList = exceptionNotifyUserUuidList;
            return this;
        }
    }
}
