package codedriver.framework.notify.dto;

public class NotifyPolicyParamVo {

	private String name;
	private String type;
	private String description;
	private int isDeletable = 1;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getIsDeletable() {
		return isDeletable;
	}
	public void setIsDeletable(int isDeletable) {
		this.isDeletable = isDeletable;
	}
}
