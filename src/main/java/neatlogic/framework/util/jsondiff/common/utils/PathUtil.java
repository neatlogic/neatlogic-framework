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

package neatlogic.framework.util.jsondiff.common.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: codeleep
 * @createTime: 2023/02/20 17:39
 * @description: 路径工具类
 */
public class PathUtil {

    private static final String OBJECT_SING = ".";

    private static final String ARRAY_SING_LEFT = "[";
    private static final String ARRAY_SING_RIGHT = "]";


    public static String getIndexPath(String index) {
        return ARRAY_SING_LEFT + index + ARRAY_SING_RIGHT;
    }


    public static String getObjectPath(String parentPath) {
        return parentPath + OBJECT_SING;
    }


    /**
     * 将下标填入path
     *
     * @param path  路径地址
     * @param index 下标
     * @return
     */
    public static String insertPathIndex(String path, int index) {
        if (path == null || path.length() == 0 || index < 0) {
            return path;
        }
        // 正则表达式匹配"]["
        Pattern pattern = Pattern.compile("\\]\\[");
        Matcher matcher = pattern.matcher(new StringBuilder(path).reverse());
        StringBuilder sb = new StringBuilder(path);
        sb.reverse();
        // 循环匹配"[]"，并记录匹配到的最右边的位置
        if (matcher.find()) {
            // 记录第一个"[]"出现的位置
            int i = matcher.start();
            sb.insert(i + 1, index); // 在"[]"中间插入数字
        }
        return sb.reverse().toString();
    }


}
