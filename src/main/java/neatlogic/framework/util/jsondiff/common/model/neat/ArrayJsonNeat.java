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
