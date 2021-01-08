package codedriver.framework.fullindex.dto;

public class DocumentOffsetVo {
	private Long fieldId;
	private String type;
	private int start;
	private int end;

	public DocumentOffsetVo() {

	}

	public Long getFieldId() {
		return fieldId;
	}

	public void setFieldId(Long fieldId) {
		this.fieldId = fieldId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DocumentOffsetVo(int _start, int _end) {
		start = _start;
		end = _end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

}
