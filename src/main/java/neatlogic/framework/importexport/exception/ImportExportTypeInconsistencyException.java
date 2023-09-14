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

package neatlogic.framework.importexport.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ImportExportTypeInconsistencyException extends ApiRuntimeException {

    private static final long serialVersionUID = 8277880771507304466L;

    public ImportExportTypeInconsistencyException(String sourceType, String targetType) {
        super("导入类型与目标类型不一致，不能将{0}类型的数据导入到{1}类型中", sourceType, targetType);
    }
}
