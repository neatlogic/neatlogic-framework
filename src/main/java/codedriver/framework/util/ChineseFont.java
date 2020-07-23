package codedriver.framework.util;

import java.net.URL;

public enum ChineseFont {

	// 仿宋体
	SIMFANG("SimFang", "simfang.ttf"),
	// 黑体
	SIMHEI("SimHei", "simhei.ttf"),
	// 楷体
	SIMKAI("SimKai", "simkai.ttf"),
	// 宋体&新宋体
	SIMSUM("SimSun", "simsun.ttc"),
	// 华文仿宋
	STFANGSO("StFangSo", "stfangso.ttf");

	private String fontFileName;
	private String fontName;

	public URL getFontUrl() {
		URL url = this.getClass().getResource("/codedriver/resources/fonts/" + this.fontFileName);
		return url;
	}

	public String getFontName() {
		return fontName;
	}

	private ChineseFont(String v, String fontFileName) {
		this.fontName = v;
		this.fontFileName = fontFileName;
	}

}
