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

package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

import java.util.List;

public class ParamNotExistsException extends ApiRuntimeException {

    private static final long serialVersionUID = -2608671566655133328L;

    public ParamNotExistsException(String paramNames) {
        super("参数：“{0}”不能为空",paramNames);
    }

    public ParamNotExistsException(int index, String keyName) {
        super("第：{0}个参数的“{2}”不能为空", index, keyName);
    }

    public ParamNotExistsException(int index, String paramName, String keyName) {
        super("第：{0}个参数“{1}”的“{2}”不能为空", index, paramName, keyName);
    }

    public ParamNotExistsException(String... paramNames) {
        super("参数：“{0}”不能同时为空", String.join("、", paramNames));
    }

    public ParamNotExistsException(List<String> eitherParamList, List<String> orParamList) {
        super("必须在[{0}]与[{1}}]两组参数中选择一组填写", String.join("、", eitherParamList), String.join("、", orParamList));
    }
}
