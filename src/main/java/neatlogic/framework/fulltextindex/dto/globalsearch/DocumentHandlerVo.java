/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.fulltextindex.dto.globalsearch;

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
