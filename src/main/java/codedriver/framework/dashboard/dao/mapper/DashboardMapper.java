package codedriver.framework.dashboard.dao.mapper;

import java.util.List;

import codedriver.framework.dashboard.dto.DashboardVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;

public interface DashboardMapper {
	public DashboardWidgetVo getDashboardWidgetByUuid(String dashboardWidgetUuid);

	public DashboardVo getDashboardByUuid(String dashboardUuid);

	public List<DashboardWidgetVo> getDashboardWidgetByDashboardUuid(String dashboardUuid);
}
