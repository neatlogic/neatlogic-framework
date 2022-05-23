/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

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

    private final String fontFileName;
    private final String fontName;

    public URL getFontUrl() {
        return this.getClass().getResource("/codedriver/resources/fonts/" + this.fontFileName);
    }

    public String getFontName() {
        return fontName;
    }

    private ChineseFont(String v, String fontFileName) {
		this.fontName = v;
		this.fontFileName = fontFileName;
	}

}
