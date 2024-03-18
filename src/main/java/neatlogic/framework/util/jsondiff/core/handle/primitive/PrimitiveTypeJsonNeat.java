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
