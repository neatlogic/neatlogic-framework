/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

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

    public String getPath() {
        return "/neatlogic/resources/fonts/" + this.fontFileName;
    }
    public URL getFontUrl() {
        return this.getClass().getResource(getPath());
    }

    public String getFontName() {
        return fontName;
    }

    private ChineseFont(String v, String fontFileName) {
		this.fontName = v;
		this.fontFileName = fontFileName;
	}

}
