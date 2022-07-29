package codedriver.framework.dao.mapper;

import codedriver.framework.dto.license.LicenseVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LicenseMapper {

    String getTenantLicenseByTenantUuid(@Param("tenantUuid") String tenantUuid);

    List<LicenseVo> getTenantLicenseByTenantUuidList(List<String> tenantUuidList);

    Integer insertLicenseByTenantUuid(@Param("tenantId") Long tenantId, @Param("tenantUuid") String tenantUuid, @Param("licenseStr") String licenseStr);
}
