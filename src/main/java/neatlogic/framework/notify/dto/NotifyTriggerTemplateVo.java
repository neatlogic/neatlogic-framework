package neatlogic.framework.notify.dto;

public class NotifyTriggerTemplateVo {

    private String trigger;
    private String title;
    private String description;
    private String content;
    private String handler;

    public NotifyTriggerTemplateVo() {}

    public NotifyTriggerTemplateVo(String trigger, String description,String title, String content, String handler) {
        this.trigger = trigger;
        this.title = title;
        this.content = content;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }
}
