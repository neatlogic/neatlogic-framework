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
