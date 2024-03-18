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

package neatlogic.framework.util.jsondiff.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.jsondiff.common.model.JsonCompareResult;
import neatlogic.framework.util.jsondiff.common.model.JsonComparedOption;
import neatlogic.framework.util.jsondiff.common.model.TravelPath;
import neatlogic.framework.util.jsondiff.core.handle.array.ComplexArrayJsonNeat;
import neatlogic.framework.util.jsondiff.core.handle.object.ComplexObjectJsonNeat;
import neatlogic.framework.util.jsondiff.core.utils.RunTimeDataFactory;

import static neatlogic.framework.util.jsondiff.common.model.Constant.PATH_ROOT;


/**
 * @author: codeleep
 * @createTime: 2023/02/25 23:43
 * @description: 面向外部的类
 */
public class DefaultJsonDifference {

    public JsonCompareResult detectDiff(JSONObject expect, JSONObject actual) {
        JsonCompareResult result = new ComplexObjectJsonNeat().diff(expect, actual, new TravelPath(PATH_ROOT));
        RunTimeDataFactory.clearOptionInstance();
        return result;
    }

    public JsonCompareResult detectDiff(JSONArray expect, JSONArray actual) {
        JsonCompareResult result = new ComplexArrayJsonNeat().diff(expect, actual, new TravelPath(PATH_ROOT));
        RunTimeDataFactory.clearOptionInstance();
        return result;
    }

    public DefaultJsonDifference option(JsonComparedOption option) {
        RunTimeDataFactory.setOptionInstance(option);
        return this;
    }

}
