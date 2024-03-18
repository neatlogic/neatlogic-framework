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
