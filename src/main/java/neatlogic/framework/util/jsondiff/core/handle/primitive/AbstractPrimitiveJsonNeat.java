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

package neatlogic.framework.util.jsondiff.core.handle.primitive;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.jsondiff.common.exception.JsonDiffException;
import neatlogic.framework.util.jsondiff.common.model.JsonCompareResult;
import neatlogic.framework.util.jsondiff.common.model.TravelPath;
import neatlogic.framework.util.jsondiff.common.model.neat.PrimitiveJsonNeat;
import neatlogic.framework.util.jsondiff.core.handle.AbstractTypeCheck;
import neatlogic.framework.util.jsondiff.core.utils.RunTimeDataFactory;

import java.util.HashSet;

public abstract class AbstractPrimitiveJsonNeat extends AbstractTypeCheck implements PrimitiveJsonNeat {

    /**
     * 路径
     */
    protected TravelPath travelPath;

    @Override
    public JsonCompareResult diff(JSONArray expect, JSONArray actual, TravelPath travelPath) {
        throw new JsonDiffException("类型调用错误");
    }

    @Override
    public JsonCompareResult diff(JSONObject expect, JSONObject actual, TravelPath travelPath) {
        throw new JsonDiffException("类型调用错误");
    }


    @Override
    public JsonCompareResult diff(Object expect, Object actual, TravelPath travelPath) {
        this.travelPath = travelPath;
        return detectDiff(expect, actual);
    }


    @Override
    public boolean check(Object expect, Object actual, JsonCompareResult result, TravelPath travelPath) {
        HashSet<String> ignorePath = RunTimeDataFactory.getOptionInstance().getIgnorePath();
        if (ignorePath.contains(travelPath.getAbstractTravelPath())) {
            return false;
        }
        return true;
    }

}
