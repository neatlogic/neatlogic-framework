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
