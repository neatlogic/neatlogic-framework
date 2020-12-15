package codedriver.framework.notify.dto;

public class NotifyTriggerTemplateVo {

    private String trigger;
    private String title;
    private String content;
    private String handler;

    public NotifyTriggerTemplateVo() {}

    public NotifyTriggerTemplateVo(String trigger, String title, String content, String handler) {
        this.trigger = trigger;
        this.title = title;
        this.content = content;
        this.handler = handler;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
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

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }
}
