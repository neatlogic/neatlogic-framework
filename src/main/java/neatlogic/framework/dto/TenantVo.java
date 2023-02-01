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

package neatlogic.framework.dto;

import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.dto.license.LicenseVo;
import neatlogic.framework.dto.module.ModuleGroupVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.restful.annotation.EntityField;
import neatlogic.framework.util.SnowflakeUtil;

import java.util.Date;
import java.util.List;

public class TenantVo extends BasePageVo {
	public enum Status {
		BUILDING, BUILDED;
	}

	@EntityField(name = "id", type = ApiParamType.LONG)
	private Long id;
	@EntityField(name = "uuid", type = ApiParamType.STRING)
	private String uuid;
	@EntityField(name = "名称", type = ApiParamType.STRING)
	private String name;
	@EntityField(name = "是否激活", type = ApiParamType.INTEGER)
	private Integer isActive;
	@EntityField(name = "描述", type = ApiParamType.STRING)
	private String description;
	@EntityField(name = "有效期限", type = ApiParamType.LONG)
	private Date expireDate;
	@EntityField(name = "激活模块", type = ApiParamType.JSONARRAY)
	private List<ModuleVo> moduleList;
	@EntityField(name = "激活模块分组", type = ApiParamType.JSONARRAY)
	private List<ModuleGroupVo> moduleGroupList;
	@EntityField(name = "状态", type = ApiParamType.STRING)
	private String status;
	@EntityField(name = "license", type = ApiParamType.JSONOBJECT)
	private LicenseVo license;
	@EntityField(name = "超级管理员", type = ApiParamType.JSONARRAY)
	private List<String> superAdminList;

	public TenantVo() {
		this.setPageSize(20);
	}

	public TenantVo(String uuid) {
		this.uuid = uuid;
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

	public Integer getIsActive() {
		return isActive;
	}

	public void setIsActive(Integer isActive) {
		this.isActive = isActive;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public List<ModuleVo> getModuleList() {
		return moduleList;
	}

	public void setModuleList(List<ModuleVo> moduleList) {
		this.moduleList = moduleList;
	}

	public Long getId() {
		if (id == null) {
			id = SnowflakeUtil.uniqueLong();
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ModuleGroupVo> getModuleGroupList() {
		return moduleGroupList;
	}

	public void setModuleGroupList(List<ModuleGroupVo> moduleGroupList) {
		this.moduleGroupList = moduleGroupList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LicenseVo getLicense() {
		return license;
	}

	public void setLicense(LicenseVo license) {
		this.license = license;
	}

	public List<String> getSuperAdminList() {
		return superAdminList;
	}

	public void setSuperAdminList(List<String> superAdminList) {
		this.superAdminList = superAdminList;
	}
}
