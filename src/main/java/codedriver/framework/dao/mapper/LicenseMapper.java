package codedriver.framework.dao.mapper;

import codedriver.framework.dto.license.LicenseVo;

import java.util.List;

public interface LicenseMapper {

    String getTenantLicenseByTenantUuid(String tenantUuid);

    List<LicenseVo> getTenantLicenseByTenantUuidList(List<String> tenantUuidList);
}
