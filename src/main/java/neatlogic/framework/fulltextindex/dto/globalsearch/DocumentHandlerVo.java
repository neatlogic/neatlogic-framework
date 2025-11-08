/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
