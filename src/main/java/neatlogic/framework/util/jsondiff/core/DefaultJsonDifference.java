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
