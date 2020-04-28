package codedriver.framework.dashboard.dao.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import codedriver.framework.dashboard.dto.DashboardVisitCounterVo;
import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;
import codedriver.framework.dto.AuthorityVo;

public interface DashboardMapper {
	public String getDefaultDashboardUuidByUserId(String userId);

	public int searchDashboardCount(DashboardVo dashboardVo);

	public List<String> searchAuthorizedDashboardUuid(DashboardVo dashboardVo);
	
	public List<DashboardVo> getDashboardByUuidList(@Param("uuidList")List<String> uuidList);

	public List<DashboardVo> searchTopVisitDashboard(DashboardVo dashboardVo);

	public int checkDashboardNameIsExists(DashboardVo dashboardVo);

	public DashboardWidgetVo getDashboardWidgetByUuid(String dashboardWidgetUuid);

	public DashboardVo getDashboardByUuid(String dashboardUuid);
	
	public DashboardVo getAuthorizedDashboardByUuid(String dashboardUuid);

	public DashboardVisitCounterVo getDashboardVisitCounter(@Param("dashboardUuid") String dashboardUuid, @Param("userId") String userId);

	public List<DashboardWidgetVo> getDashboardWidgetByDashboardUuid(String dashboardUuid);

	public int updateDashboard(DashboardVo dashboardVo);

	public int updateDashboardVisitCounter(DashboardVisitCounterVo dashboardVisitCounterVo);

	public int insertDashboard(DashboardVo dashboardVo);

	public int insertDashboardAuthority(@Param("authorityVo")AuthorityVo authorityVo,@Param("dashboardUuid") String dashboardUuid);
	
	public int insertDashboardWidget(DashboardWidgetVo dashboardWidgetVo);

	public int insertDashboardDefault(@Param("dashboardUuid") String dashboardUuid, @Param("userId") String userId, @Param("type") String type);

	public int insertDashboardVisitCounter(DashboardVisitCounterVo dashboardVisitCounterVo);

	public int deleteDashboardByUuid(String dashboardUuid);

	public int deleteDashboardWidgetByDashboardUuid(String dashboardUuid);

	public int deleteDashboardAuthorityByUuid(@Param("dashboardUuid")String dashboardUuid);

	public int deleteDashboardDefaultByDashboardUuid(String dashboardUuid);

	public int deleteDashboardVisitCounterByDashboardUuid(String dashboardUuid);

	public int deleteDashboardDefaultByUserId(String userId);
}
