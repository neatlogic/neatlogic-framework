package codedriver.framework.dashboard.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;

public interface DashboardMapper {

	public int searchDashboardCount(DashboardVo dashboardVo);

	public List<DashboardVo> searchDashboard(DashboardVo dashboardVo);

	public List<String> getDashboardRoleByDashboardUuid(@Param("dashboardUuid") String dashboardUuid, @Param("userId") String userId);

	public int checkDashboardNameIsExists(DashboardVo dashboardVo);

	public DashboardWidgetVo getDashboardWidgetByUuid(String dashboardWidgetUuid);

	public DashboardVo getDashboardByUuid(String dashboardUuid);

	public List<DashboardWidgetVo> getDashboardWidgetByDashboardUuid(String dashboardUuid);

	public int updateDashboard(DashboardVo dashboardVo);

	public int insertDashboard(DashboardVo dashboardVo);

	public int insertDashboardWidget(DashboardWidgetVo dashboardWidgetVo);

	public int deleteDashboardByUuid(String dashboardUuid);

	public int deleteDashboardWidgetByDashboardUuid(String dashboardUuid);

	public int deleteDashboardRoleByDashboardUuid(String dashboardUuid);

	public int deleteDashboardDefaultUserByDashboardUuid(String dashboardUuid);
}
