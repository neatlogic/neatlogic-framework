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

package neatlogic.framework.util.jsondiff.core.handle.array;

import com.alibaba.fastjson.JSONArray;
import neatlogic.framework.util.jsondiff.common.model.Defects;
import neatlogic.framework.util.jsondiff.common.model.JsonCompareResult;
import neatlogic.framework.util.jsondiff.common.model.TravelPath;
import neatlogic.framework.util.jsondiff.common.model.neat.JsonNeat;
import neatlogic.framework.util.jsondiff.core.utils.ClassUtil;
import neatlogic.framework.util.jsondiff.core.utils.JsonDiffUtil;
import neatlogic.framework.util.jsondiff.core.utils.RunTimeDataFactory;

import static neatlogic.framework.util.jsondiff.common.model.Constant.DATA_TYPE_INCONSISTENT;
import static neatlogic.framework.util.jsondiff.common.model.Constant.INCONSISTENT_ARRAY_LENGTH;


public class ComplexArrayJsonNeat extends AbstractArrayJsonNeat {

    /**
     * 当前节点比较结果
     */
    private final JsonCompareResult result = new JsonCompareResult();

    @Override
    public JsonCompareResult detectDiff(JSONArray expect, JSONArray actual) {
        // 前置校验失败
        if (!check(expect, actual, result, travelPath)) {
            return result;
        }
        // 长度不一致
        int expectSize = ((JSONArray) expect).size();
        int actualSize = ((JSONArray) actual).size();
        if (expectSize != actualSize) {
            Defects defects = new Defects()
                    .setActual(actualSize)
                    .setExpect(expectSize)
                    .setTravelPath(travelPath)
                    .setIllustrateTemplate(INCONSISTENT_ARRAY_LENGTH, String.valueOf(expectSize), String.valueOf(actualSize));
            result.addDefects(defects);
        }

        boolean ignoreOrder = RunTimeDataFactory.getOptionInstance().isIgnoreOrder();
        // 测试
        if (ignoreOrder) {
            return ignoreOrder(expect, actual);
        }
        return keepOrder(expect, actual);
    }

    @Override
    public JsonCompareResult ignoreOrder(JSONArray expect, JSONArray actual) {
        int expectIndex = 0;
        int actualIndex = 0;
        int expectLen = expect.size();
        int actualLen = actual.size();
        boolean[] expectFlag = new boolean[expectLen];
        boolean[] actualFlag = new boolean[expectLen];


        // 判断出所有能匹配的数据
        for (expectIndex = 0; expectIndex < expectLen; expectIndex++) {
            if (expectFlag[expectIndex]) {
                continue;
            }
            for (actualIndex = 0; actualIndex < Math.min(expectLen, actualLen); actualIndex++) {
                if (actualFlag[actualIndex]) {
                    continue;
                }
                TravelPath nextTravelPath = new TravelPath(this.travelPath, expectIndex, actualIndex);
                JsonNeat jsonNeat = JsonDiffUtil.getJsonNeat(expect.get(expectIndex), actual.get(actualIndex), nextTravelPath);
                if (jsonNeat == null) {
                    continue;
                }
                JsonCompareResult compareResult = jsonNeat.diff(expect.get(expectIndex), actual.get(actualIndex), nextTravelPath);
                if (compareResult != null && compareResult.isMatch()) {
                    expectFlag[expectIndex] = true;
                    actualFlag[actualIndex] = true;
                    break;
                }
            }
        }

        // 对所有未匹配数据进行报错
        for (expectIndex = 0; expectIndex < expectLen; expectIndex++) {
            if (expectFlag[expectIndex]) {
                continue;
            }
            for (actualIndex = 0; actualIndex < Math.min(expectLen, actualLen); actualIndex++) {
                if (actualFlag[actualIndex]) {
                    continue;
                }
                Object expectItem = expect.get(expectIndex);
                Object actualItem = actual.get(actualIndex);
                TravelPath nextTravelPath = new TravelPath(this.travelPath, expectIndex, actualIndex);
                // 判断类型, 根据类型去实例化JsonNeat。
                JsonNeat jsonNeat = JsonDiffUtil.getJsonNeat(expectItem, actualItem, nextTravelPath);
                // 类型不一致
                if (jsonNeat != null) {
                    JsonCompareResult diff = jsonNeat.diff(expectItem, actualItem, nextTravelPath);
                    // 将结果合并
                    if (!diff.isMatch()) {
                        result.mergeDefects(diff.getDefectsList());
                    }
                    continue;
                }
                Defects defects = new Defects()
                        .setActual(actualItem)
                        .setExpect(expectItem)
                        .setTravelPath(nextTravelPath)
                        .setIllustrateTemplate(DATA_TYPE_INCONSISTENT, ClassUtil.getClassName(expectItem), ClassUtil.getClassName(actualItem));
                result.addDefects(defects);
            }
        }
        return result;
    }

    @Override
    public JsonCompareResult keepOrder(JSONArray expect, JSONArray actual) {
        int len = expect.size();
        for (int i = 0; i < len; i++) {
            Object expectItem = expect.get(i);
            Object actualItem = actual.get(i);
            TravelPath nextTravelPath = new TravelPath(this.travelPath, i, i);
            // 判断类型, 根据类型去实例化JsonNeat。
            JsonNeat jsonNeat = JsonDiffUtil.getJsonNeat(expectItem, actualItem, nextTravelPath);
            // 类型不一致
            if (jsonNeat == null) {
                Defects defects = new Defects()
                        .setExpect(expectItem)
                        .setActual(actualItem)
                        .setTravelPath(nextTravelPath)
                        .setIllustrateTemplate(DATA_TYPE_INCONSISTENT, ClassUtil.getClassName(expectItem), ClassUtil.getClassName(actualItem));
                result.addDefects(defects);
                continue;
            }
            JsonCompareResult diff = jsonNeat.diff(expectItem, actualItem, nextTravelPath);
            // 将结果合并
            if (!diff.isMatch()) {
                result.mergeDefects(diff.getDefectsList());
            }
        }
        return result;
    }

}
