package codedriver.framework.dashboard.core;

import org.springframework.util.ClassUtils;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.dashboard.dto.ChartDataVo;
import codedriver.framework.dashboard.dto.DashboardWidgetVo;

public interface IDashboardHandler {

	public String getType();

	public default String getClassName() {
		return ClassUtils.getUserClass(this.getClass()).getName();
	}

	/**
	 * @Time:Mar 2, 2020
	 * @Description: 获取唯一名
	 * @param @return
	 * @return String
	 */
	public String getName();
	
	/**
	* @Author: chenqiwei
	* @Time:Mar 20, 2020
	* @Description: 获取图标 
	* @param @return 
	* @return String
	 */
	public String getIcon();
	
	/**
	* @Author: chenqiwei
	* @Time:Mar 20, 2020
	* @Description: 获取显示名 
	* @param @return 
	* @return String
	 */
	public String getDisplayName();

	/**
	 * @Time:Mar 2, 2020
	 * @Description: 获取数据
	 * @param @return
	 * @return ChartDataVo
	 */
	public ChartDataVo getData(DashboardWidgetVo dashboardWidgetVo);

	/**
	 * @Time:Mar 2, 2020
	 * @Description: TODO 获取图表视图配置数据
	 * @param @return
	 * @return JSONObject
	 */
	public JSONObject getConfig(DashboardWidgetVo widgetVo);
	
}
