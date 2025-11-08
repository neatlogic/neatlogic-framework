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

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class HanyuPinyinUtil {

    public static String format(String source) {
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 不带声调
        format.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] ch = source.trim().toCharArray();
        StringBuffer buffer = new StringBuffer("");
        for (int i = 0; i < ch.length; i++) {
            if (Character.toString(ch[i]).matches("[\u4e00-\u9fa5]")) {
                String[] temp;
                try {
                    temp = PinyinHelper.toHanyuPinyinStringArray(ch[i], format);
                    buffer.append(temp[0]);
                } catch (BadHanyuPinyinOutputFormatCombination | NullPointerException e) {
                    // 无法翻译的生僻字，不必处理
                }
            } else {
                buffer.append(Character.toString(ch[i]));
            }
        }
        return buffer.toString();
    }
}
