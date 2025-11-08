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

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.jsondiff.common.model.JsonCompareResult;

public interface ObjectJsonNeat extends JsonNeat {

    /**
     * 比较对象
     *
     * @param expect 期望的json对象
     * @param actual 实际的json对象
     * @return 返回比较结果
     * @throws IllegalAccessException 发生异常直接抛出
     */
    JsonCompareResult detectDiff(JSONObject expect, JSONObject actual);

}
