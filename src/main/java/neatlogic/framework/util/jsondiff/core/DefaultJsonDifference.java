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
