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

package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ExcelFormatIllegalException extends ApiRuntimeException {


    public ExcelFormatIllegalException() {
        super("Excel文件格式错误，请上传xls或xlsx格式的Excel文件");
    }

    public ExcelFormatIllegalException(String format) {
        super("Excel文件格式错误，请上传" + format + "格式的Excel文件");
    }

}
