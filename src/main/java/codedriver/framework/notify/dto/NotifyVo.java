package codedriver.framework.notify.dto;

import java.util.ArrayList;
import java.util.List;

import codedriver.framework.message.core.IMessageHandler;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.notify.core.INotifyTriggerType;
import codedriver.framework.util.FreemarkerUtil;

public class NotifyVo {
    private Long id;
    private INotifyTriggerType triggerType;
    private Class<? extends IMessageHandler> newsHandlerClass;
    private String title;
    private String content;
    private List<NotifyReceiverVo> notifyReceiverVoList;
    private String fromUser;
    private String fromUserEmail;
    private JSONObject data = new JSONObject();
    private String templateContent;
    private String templateTitle;

    private List<String> exceptionNotifyUserUuidList;
    private StringBuilder errorBuilder;

    private String config;

    private NotifyVo(Builder builder) {
        this.triggerType = builder.triggerType;
        this.newsHandlerClass = builder.newsHandlerClass;
        this.data = builder.data;
        this.notifyReceiverVoList = builder.notifyReceiverVoList;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTemplateContent() {
        return templateContent;
    }

    public String getTemplateTitle() {
        return templateTitle;
    }

    public List<NotifyReceiverVo> getNotifyReceiverVoList() {
        return notifyReceiverVoList;
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

    public Class<? extends IMessageHandler> getNewsHandlerClass() {
        return this.newsHandlerClass;
    }

    public String getConfig() {
        if (StringUtils.isBlank(config)) {
            JSONObject configObj = new JSONObject();
            configObj.put("notifyReceiverVoList", notifyReceiverVoList);
            configObj.put("exceptionNotifyUserUuidList", exceptionNotifyUserUuidList);
            config = configObj.toJSONString();
        }
        return config;
    }

    public static class Builder {
        // 可选参数
        private String templateContent;
        private String templateTitle;
        private JSONObject data = new JSONObject();
        private List<NotifyReceiverVo> notifyReceiverVoList = new ArrayList<>();
        private List<String> exceptionNotifyUserUuidList = new ArrayList<>();

        private INotifyTriggerType triggerType;
        private Class<? extends IMessageHandler> newsHandlerClass;

        public Builder(INotifyTriggerType _triggerType, Class<? extends IMessageHandler> _newsHandlerClass) {
            this.triggerType = _triggerType;
            this.newsHandlerClass = _newsHandlerClass;
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

        public Builder addNotifyReceiverVo(NotifyReceiverVo notifyReceiverVo) {
            if (!this.notifyReceiverVoList.contains(notifyReceiverVo)) {
                this.notifyReceiverVoList.add(notifyReceiverVo);
            }
            return this;
        }

        public Builder addAllNotifyReceiverVo(List<NotifyReceiverVo> notifyReceiverVoList) {
            for (NotifyReceiverVo notifyReceiverVo : notifyReceiverVoList) {
                addNotifyReceiverVo(notifyReceiverVo);
            }
            return this;
        }

        public Builder setExceptionNotifyUserUuidList(List<String> exceptionNotifyUserUuidList) {
            this.exceptionNotifyUserUuidList = exceptionNotifyUserUuidList;
            return this;
        }
    }
}
