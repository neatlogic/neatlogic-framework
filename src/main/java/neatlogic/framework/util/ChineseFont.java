/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.util;

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
        return this.getClass().getResource("/neatlogic/resources/fonts/" + this.fontFileName);
    }

    public String getFontName() {
        return fontName;
    }

    private ChineseFont(String v, String fontFileName) {
		this.fontName = v;
		this.fontFileName = fontFileName;
	}

}
