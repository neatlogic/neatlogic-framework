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

package neatlogic.framework.util.javascript;

/** GraalJS 对外异常，携带已经转换为普通 Java 字段的脚本诊断。 */
public class GraalJsException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final GraalJsErrorType errorType;
    private final Integer startLine;
    private final Integer startColumn;
    private final Integer endLine;
    private final Integer endColumn;
    private final String sourceSnippet;
    private final String scriptStack;

    public GraalJsException(
            GraalJsErrorType errorType,
            String message,
            Integer startLine,
            Integer startColumn,
            Integer endLine,
            Integer endColumn,
            String sourceSnippet,
            String scriptStack,
            Throwable cause
    ) {
        super(message, cause);
        this.errorType = errorType;
        this.startLine = startLine;
        this.startColumn = startColumn;
        this.endLine = endLine;
        this.endColumn = endColumn;
        this.sourceSnippet = sourceSnippet;
        this.scriptStack = scriptStack;
    }

    public GraalJsErrorType getErrorType() {
        return errorType;
    }

    public Integer getStartLine() {
        return startLine;
    }

    public Integer getStartColumn() {
        return startColumn;
    }

    public Integer getEndLine() {
        return endLine;
    }

    public Integer getEndColumn() {
        return endColumn;
    }

    public String getSourceSnippet() {
        return sourceSnippet;
    }

    public String getScriptStack() {
        return scriptStack;
    }
}
