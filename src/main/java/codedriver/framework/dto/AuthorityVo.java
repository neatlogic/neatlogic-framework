package codedriver.framework.dto;

public class AuthorityVo {
	private String type;
	private String uuid;

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public AuthorityVo() {

	}
	public AuthorityVo(String type, String uuid) {
		this.type = type;
		this.uuid = uuid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AuthorityVo that = (AuthorityVo) o;
		if (type == null) {
			if (that.type != null)
				return false;
		} else if (!type.equals(that.type))
			return false;
		if (uuid == null) {
			if (that.uuid != null)
				return false;
		} else if (!uuid.equals(that.uuid))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}
}
