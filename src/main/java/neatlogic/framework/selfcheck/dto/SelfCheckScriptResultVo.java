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

package neatlogic.framework.selfcheck.dto;

import java.util.ArrayList;
import java.util.List;

/** 自检 JavaScript 编译或执行结果。 */
public class SelfCheckScriptResultVo {
    private String status;
    private Boolean success;
    private Boolean valid;
    private String message;
    private Object rawResult;
    private Long durationMs;
    private Boolean logTruncated = false;
    private List<SelfCheckScriptDiagnosticVo> diagnosticList = new ArrayList<>();
    private List<SelfCheckScriptLogVo> consoleList = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRawResult() {
        return rawResult;
    }

    public void setRawResult(Object rawResult) {
        this.rawResult = rawResult;
    }

    public Long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(Long durationMs) {
        this.durationMs = durationMs;
    }

    public Boolean getLogTruncated() {
        return logTruncated;
    }

    public void setLogTruncated(Boolean logTruncated) {
        this.logTruncated = logTruncated;
    }

    public List<SelfCheckScriptDiagnosticVo> getDiagnosticList() {
        return diagnosticList;
    }

    public void setDiagnosticList(List<SelfCheckScriptDiagnosticVo> diagnosticList) {
        this.diagnosticList = diagnosticList;
    }

    public void addDiagnostic(SelfCheckScriptDiagnosticVo diagnosticVo) {
        if (diagnosticVo != null) {
            diagnosticList.add(diagnosticVo);
        }
    }

    public List<SelfCheckScriptLogVo> getConsoleList() {
        return consoleList;
    }

    public void setConsoleList(List<SelfCheckScriptLogVo> consoleList) {
        this.consoleList = consoleList;
    }
}
