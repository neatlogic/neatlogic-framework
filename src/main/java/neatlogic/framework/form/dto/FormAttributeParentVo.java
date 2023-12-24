package neatlogic.framework.form.dto;

public class FormAttributeParentVo {
    private String uuid;
    private String name;
    private FormAttributeParentVo parent;

    public FormAttributeParentVo() {

    }

    public FormAttributeParentVo(String uuid, String name, FormAttributeParentVo parent) {
        this.uuid = uuid;
        this.name = name;
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

    public FormAttributeParentVo getParent() {
        return parent;
    }

    public void setParent(FormAttributeParentVo parent) {
        this.parent = parent;
    }
}
