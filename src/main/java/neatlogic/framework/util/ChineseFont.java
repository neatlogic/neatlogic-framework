/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
