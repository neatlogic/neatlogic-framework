/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.exception.file;

import neatlogic.framework.exception.core.ApiRuntimeException;

public class ExcelFormatIllegalException extends ApiRuntimeException {

    private static final long serialVersionUID = -5415989970048167194L;

    public ExcelFormatIllegalException() {
        super("Excel文件格式错误，请上传xls或xlsx格式的Excel文件");
    }

    public ExcelFormatIllegalException(String format) {
        super("Excel文件格式错误，请上传{0}格式的Excel文件", format);
    }

}
