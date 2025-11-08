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

package neatlogic.framework.util.jsondiff.common.model.neat;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.util.jsondiff.common.model.JsonCompareResult;

public interface ArrayJsonNeat extends JsonNeat {

    /**
     * 比较数组
     *
     * @param expect 期望的json对象
     * @param actual 实际的json对象
     * @return 返回比较结果
     */
    JsonCompareResult detectDiff(JSONArray expect, JSONArray actual);

    // 忽略顺序的比较
    JsonCompareResult ignoreOrder(JSONArray expect, JSONArray actual);

    // 保持顺序比较
    JsonCompareResult keepOrder(JSONArray expect, JSONArray actual);

}
