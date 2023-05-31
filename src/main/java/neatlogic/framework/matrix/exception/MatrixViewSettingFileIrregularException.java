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

package neatlogic.framework.matrix.exception;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class MatrixViewSettingFileIrregularException extends ApiRuntimeException {
    private static final long serialVersionUID = 5166847622344597434L;

    public MatrixViewSettingFileIrregularException(Exception ex) {
        super("exception.framework.matrixviewsettingfileirregularexception.b", ex.getMessage());
    }

    public MatrixViewSettingFileIrregularException(String nodeName) {
        super("exception.framework.matrixviewsettingfileirregularexception.c", nodeName);
    }

    public MatrixViewSettingFileIrregularException(String nodeName, String attrName) {
        super("exception.framework.matrixviewsettingfileirregularexception.d", nodeName, attrName);
    }
}
