/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.fulltextindex.dto.globalsearch;

public class DocumentHandlerVo {
	private String type;
	private String name;
	private int documentCount;
	private String moduleName;
	private RebuildAuditVo rebuildAudit;

	public DocumentHandlerVo() {

	}

	public DocumentHandlerVo(String _type, String _name, String _moduleName) {
		this.type = _type;
		this.name = _name;
		this.moduleName = _moduleName;
	}

	public int getDocumentCount() {
		return documentCount;
	}

	public void setDocumentCount(int documentCount) {
		this.documentCount = documentCount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RebuildAuditVo getRebuildAudit() {
		return rebuildAudit;
	}

	public void setRebuildAudit(RebuildAuditVo rebuildAudit) {
		this.rebuildAudit = rebuildAudit;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

}
