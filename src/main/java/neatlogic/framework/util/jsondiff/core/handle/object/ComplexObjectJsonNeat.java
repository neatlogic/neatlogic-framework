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

package neatlogic.framework.util.jsondiff.core.handle.object;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.util.jsondiff.common.model.Defects;
import neatlogic.framework.util.jsondiff.common.model.JsonCompareResult;
import neatlogic.framework.util.jsondiff.common.model.MappingKey;
import neatlogic.framework.util.jsondiff.common.model.TravelPath;
import neatlogic.framework.util.jsondiff.common.model.neat.JsonNeat;
import neatlogic.framework.util.jsondiff.common.utils.PathUtil;
import neatlogic.framework.util.jsondiff.core.utils.ClassUtil;
import neatlogic.framework.util.jsondiff.core.utils.JsonDiffUtil;
import neatlogic.framework.util.jsondiff.core.utils.RunTimeDataFactory;

import java.util.*;
import java.util.stream.Collectors;

import static neatlogic.framework.util.jsondiff.common.model.Constant.DATA_TYPE_INCONSISTENT;
import static neatlogic.framework.util.jsondiff.common.model.Constant.SEPARATE_KEY;


/**
 * @author: codeleep
 * @createTime: 2023/02/19 19:21
 * @description: 比较复杂的json对象
 */
public class ComplexObjectJsonNeat extends AbstractObjectJsonNeat {

    /**
     * 应比较集合
     * key: actual 的 key
     * value: expect 的 key
     */
    private final List<MappingKey> keyMap = new ArrayList<>();


    /**
     * 当前节点比较结果
     */
    private final JsonCompareResult result = new JsonCompareResult();


    @Override
    public JsonCompareResult detectDiff(JSONObject expect, JSONObject actual) {
        // 前置校验失败
        if (!check(expect, actual, result, travelPath)) {
            return result;
        }
        // 计算出; 应该比较的key集合
        keySetConversion(expect.keySet(), actual.keySet());
        // 遍历比较
        for (MappingKey mappingKey : keyMap) {
            // 未找到另外一个key。只存存在其中一个为null情况
            if (mappingKey.getExpectKey() == null || mappingKey.getActualKey() == null) {
                Defects defects = new Defects()
                        .setActual(actual.get(mappingKey.getActualKey()))
                        .setExpect(expect.get(mappingKey.getExpectKey()))
                        .setTravelPath(new TravelPath(travelPath, mappingKey))
                        .setIllustrateTemplate(SEPARATE_KEY, mappingKey.getExpectKey(), mappingKey.getActualKey());
                result.addDefects(defects);
                continue;
            }

            Object expectDiffJson = expect.get(mappingKey.getExpectKey());
            Object actualDiffJson = actual.get(mappingKey.getActualKey());
            TravelPath nextTravelPath = new TravelPath(travelPath, mappingKey);
            // 判断类型, 根据类型去实例化JsonNeat。
            JsonNeat jsonNeat = JsonDiffUtil.getJsonNeat(expectDiffJson, actualDiffJson, nextTravelPath);
            if (jsonNeat == null) {
                Defects defects = new Defects()
                        .setActual(actualDiffJson)
                        .setExpect(expectDiffJson)
                        .setTravelPath(nextTravelPath)
                        .setIllustrateTemplate(DATA_TYPE_INCONSISTENT, ClassUtil.getClassName(expectDiffJson), ClassUtil.getClassName(actualDiffJson));
                result.addDefects(defects);
                continue;
            }
            // 比较非基础类型
            JsonCompareResult diff = jsonNeat.diff(expectDiffJson, actualDiffJson, nextTravelPath);
            // 将结果合并
            if (!diff.isMatch()) {
                result.mergeDefects(diff.getDefectsList());
            }
        }
        return result;
    }

    /**
     * 计算出两个应该比较的key集合
     *
     * @param expectKeys
     * @param actualKeys
     * @return
     */
    private void keySetConversion(Set<String> expectKeys, Set<String> actualKeys) {
        // 应该遍历的Key
        HashSet<String> neatExpectKeys = new HashSet<>();
        HashSet<String> neatActualKeys = new HashSet<>();
        // 字段映射
        Map<String, String> mapping = RunTimeDataFactory.getOptionInstance().getMapping();
        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            if (actualKeys.contains(entry.getKey()) && expectKeys.contains(entry.getValue())) {
                neatActualKeys.add(entry.getKey());
                neatExpectKeys.add(entry.getValue());
                keyMap.add(new MappingKey(entry.getValue(), entry.getKey()));
            }
        }
        // 忽略的key
        HashSet<String> ignoreKey = RunTimeDataFactory.getOptionInstance().getIgnoreKey();
        // 遍历期望
        for (String key : expectKeys) {
            if (ignoreKey.contains(key) || neatExpectKeys.contains(key)) {
                continue;
            }
            neatExpectKeys.add(key);
            if (actualKeys.contains(key)) {
                neatActualKeys.add(key);
            }
            keyMap.add(new MappingKey(key, actualKeys.contains(key) ? key : null));
        }
        for (String key : actualKeys) {
            if (ignoreKey.contains(key) || neatActualKeys.contains(key)) {
                continue;
            }
            neatActualKeys.add(key);
            keyMap.add(new MappingKey(expectKeys.contains(key) ? key : null, key));
        }
        // 移除忽略的Path
        HashSet<String> ignorePath = RunTimeDataFactory.getOptionInstance().getIgnorePath();
        List<MappingKey> mappingKeys = keyMap.stream().filter(mappingKey -> {
            String actualTravelPath = PathUtil.getObjectPath(travelPath.getAbstractTravelPath()) + mappingKey.getActualKey();
            String expectTravelPath = PathUtil.getObjectPath(travelPath.getAbstractTravelPath()) + mappingKey.getActualKey();
            if (ignorePath.contains(actualTravelPath) || ignorePath.contains(expectTravelPath)) {
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        // 清理
        keyMap.clear();
        keyMap.addAll(mappingKeys);
    }


}
