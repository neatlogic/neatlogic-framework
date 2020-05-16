package codedriver.framework.dashboard.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class DashboardChartFactory {
	private static Map<String, DashboardChartBase> chartMap = new HashMap<>();
	private static List<DashboardChartBase> charList = new ArrayList<DashboardChartBase>();
	static {
		Reflections reflections = new Reflections("codedriver.framework.dashboard.core.charts");
		Set<Class<? extends DashboardChartBase>> modules = reflections.getSubTypesOf(DashboardChartBase.class);
		for (Class<? extends DashboardChartBase> c : modules) {
			DashboardChartBase chart;
			try {
				chart = c.newInstance();
				for (String ch : chart.getSupportChart()) {
					chartMap.put(ch, chart);
					charList.add(chart);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static DashboardChartBase getChart(String type) {
		return chartMap.get(type);
	}
	
	public static List<DashboardChartBase> getDashboardChartList() {
		return charList;
	}
}
