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

public class ParamIrregularException extends ApiRuntimeException {
    private static final long serialVersionUID = 6665736901978853670L;

    public ParamIrregularException(String paramName) {
        super("参数“" + paramName + "”不符合格式要求");
    }

    public ParamIrregularException(String paramName, String rule) {
        super("参数“" + paramName + "”不符合格式要求， 格式为：" + rule);
    }

}
