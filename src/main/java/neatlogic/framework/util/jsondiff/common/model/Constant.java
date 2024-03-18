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

package neatlogic.framework.util.jsondiff.common.model;

public class Constant {


    /**
     * *************************************************************************
     * 遍历所需常量
     * **************************************************************************
     */
    public static final String PATH_ROOT = "root";
    public static final String SIGN = ".";
    public static final String JOIN_SPILT = "@_^_@";
    public static final String NULL = "null";


    /**
     * *************************************************************************
     * 提示信息
     * **************************************************************************
     */
    // 预期和实际不一致
    public static final String DATA_INCONSISTENT = "The expect('%s') data is inconsistent with the actual('%s') data";
    // 预期的值类型和实际的值类型不一致
    public static final String DATA_TYPE_INCONSISTENT = "The expect type ('%s') is inconsistent with the actual type ('%s')";

    public static final String INCONSISTENT_ARRAY_LENGTH = "The expect array length ('%s') is inconsistent with the actual array length ('%s')";

    // 发现异常
    public static final String FINDING_ANOMALY = "expect('%s') are found when comparing the actual('%s') and expected results。msg: %s";

    // 只存在于一个集合的key
    public static final String SEPARATE_KEY = "Only one set of keys exists expect('%s'),actual('%s')";

}
