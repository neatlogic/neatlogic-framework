/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
