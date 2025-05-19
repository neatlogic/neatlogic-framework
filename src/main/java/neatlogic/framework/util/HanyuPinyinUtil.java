/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
