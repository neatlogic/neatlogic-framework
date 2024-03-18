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

/**
 * 主要用于翻译封装字符串
 */
public class $ {
    /**
     * 翻译
     *
     * @param key 翻译的key
     * @return 翻译后的值
     */
    public static String t(String key) {
        return I18nUtils.getMessage(key);
    }

    /**
     * 翻译
     *
     * @param key  翻译的key
     * @param args key里面需要替换的变量
     * @return 翻译后的值
     */
    public static String t(String key, Object... args) {
        return I18nUtils.getMessage(key, args);
    }
}
