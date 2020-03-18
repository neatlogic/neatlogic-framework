package codedriver.framework.dashboard.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import codedriver.framework.dashboard.dto.DashboardRoleVo;
import codedriver.framework.dashboard.dto.DashboardVisitCounterVo;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;
import codedriver.framework.elasticsearch.annotation.ElasticSearch;

public interface DashboardMapper {
	public String getDefaultDashboardUuidByUserId(String userId);

	public int searchDashboardCount(DashboardVo dashboardVo);

	public List<DashboardVo> searchDashboard(DashboardVo dashboardVo);

	public List<DashboardVo> searchTopVisitDashboard(DashboardVo dashboardVo);

	public List<String> getDashboardRoleByDashboardUuidAndUserId(@Param("dashboardUuid") String dashboardUuid, @Param("userId") String userId);

	public List<DashboardRoleVo> getDashboardRoleByDashboardUuid(String dashboardUuid);

	public int checkDashboardNameIsExists(DashboardVo dashboardVo);

	public DashboardWidgetVo getDashboardWidgetByUuid(String dashboardWidgetUuid);

	public DashboardVo getDashboardByUuid(String dashboardUuid);

	public DashboardVisitCounterVo getDashboardVisitCounter(@Param("dashboardUuid") String dashboardUuid, @Param("userId") String userId);

	public List<DashboardWidgetVo> getDashboardWidgetByDashboardUuid(String dashboardUuid);

	public int updateDashboard(DashboardVo dashboardVo);

	public int updateDashboardVisitCounter(DashboardVisitCounterVo dashboardVisitCounterVo);

	public int insertDashboard(DashboardVo dashboardVo);

	public int insertDashboardRole(DashboardRoleVo dashboardRoleVo);

	public int insertDashboardWidget(DashboardWidgetVo dashboardWidgetVo);

	public int insertDashboardDefault(@Param("dashboardUuid") String dashboardUuid, @Param("userId") String userId);

	public int insertDashboardVisitCounter(DashboardVisitCounterVo dashboardVisitCounterVo);

	public int deleteDashboardByUuid(String dashboardUuid);

	public int deleteDashboardWidgetByDashboardUuid(String dashboardUuid);

	public int deleteDashboardRoleByDashboardUuid(String dashboardUuid);

	public int deleteDashboardDefaultUserByDashboardUuid(String dashboardUuid);

	public int deleteDashboardVisitCounterByDashboardUuid(String dashboardUuid);

	public int deleteDashboardDefaultUser(@Param("dashboardUuid") String dashboardUuid, @Param("userId") String userId);
}
