package neatlogic.framework.form.dto;

public class FormAttributeParentVo {
    private String uuid;
    private String name;
    private String handler;
    private FormAttributeParentVo parent;

    public FormAttributeParentVo() {

    }

    public FormAttributeParentVo(String uuid, String name, String handler, FormAttributeParentVo parent) {
        this.uuid = uuid;
        this.name = name;
        this.handler = handler;
        this.parent = parent;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public FormAttributeParentVo getParent() {
        return parent;
    }

    public void setParent(FormAttributeParentVo parent) {
        this.parent = parent;
    }
}
