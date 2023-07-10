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

import neatlogic.framework.util.jsondiff.common.exception.JsonDiffException;
import neatlogic.framework.util.jsondiff.common.model.Defects;
import neatlogic.framework.util.jsondiff.common.model.JsonCompareResult;
import neatlogic.framework.util.jsondiff.core.utils.ClassUtil;

import static neatlogic.framework.util.jsondiff.common.model.Constant.DATA_INCONSISTENT;
import static neatlogic.framework.util.jsondiff.common.model.Constant.DATA_TYPE_INCONSISTENT;


public class PrimitiveTypeJsonNeat extends AbstractPrimitiveJsonNeat {

    @Override
    public JsonCompareResult detectDiff(Object expect, Object actual) {
        JsonCompareResult result = new JsonCompareResult();
        if (!check(expect, actual, result, travelPath)) {
            return result;
        }
        if (!ClassUtil.isPrimitiveType(expect) || !ClassUtil.isPrimitiveType(actual)) {
            throw new JsonDiffException("类型调用错误");
        }


        // 都为null
        if (expect == null && actual == null) {
            return result;
        }
        // class不一致
        if (!ClassUtil.isSameClass(expect, actual)) {
            Defects defects = new Defects()
                    .setActual(actual)
                    .setExpect(expect)
                    .setTravelPath(travelPath)
                    .setIllustrateTemplate(DATA_TYPE_INCONSISTENT, ClassUtil.getClassName(expect), ClassUtil.getClassName(actual));
            result.addDefects(defects);
            return result;
        }

        // 值一样
        if (expect.equals(actual)) {
            return result;
        }

        // 值不一致
        Defects defects = new Defects()
                .setActual(actual)
                .setExpect(expect)
                .setTravelPath(travelPath)
                .setIllustrateTemplate(DATA_INCONSISTENT, String.valueOf(expect), String.valueOf(actual));
        result.addDefects(defects);
        return result;
    }

}