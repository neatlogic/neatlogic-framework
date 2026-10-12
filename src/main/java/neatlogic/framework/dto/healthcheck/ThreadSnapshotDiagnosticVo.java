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

package neatlogic.framework.dto.healthcheck;

import java.util.ArrayList;
import java.util.List;

/**
 * 诊断结果使用提示码和证据，由界面统一翻译展示。
 */
public class ThreadSnapshotDiagnosticVo {
    /** 诊断提示码。 */
    private String code;
    /** 诊断级别。 */
    private String severity;
    /** 相关锁。 */
    private String lockName;
    /** 锁持有者标识。 */
    private String ownerId;
    /** 相关线程标识。 */
    private List<String> threadIds = new ArrayList<>();
    /** 诊断证据。 */
    private List<String> evidence = new ArrayList<>();

    /** 获取诊断提示码。 */
    public String getCode() {
        return code;
    }

    /** 设置诊断提示码。 */
    public void setCode(String code) {
        this.code = code;
    }

    /** 获取诊断级别。 */
    public String getSeverity() {
        return severity;
    }

    /** 设置诊断级别。 */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /** 获取相关锁。 */
    public String getLockName() {
        return lockName;
    }

    /** 设置相关锁。 */
    public void setLockName(String lockName) {
        this.lockName = lockName;
    }

    /** 获取锁持有者标识。 */
    public String getOwnerId() {
        return ownerId;
    }

    /** 设置锁持有者标识。 */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /** 获取相关线程标识。 */
    public List<String> getThreadIds() {
        return threadIds;
    }

    /** 设置相关线程标识。 */
    public void setThreadIds(List<String> threadIds) {
        this.threadIds = threadIds;
    }

    /** 获取诊断证据。 */
    public List<String> getEvidence() {
        return evidence;
    }

    /** 设置诊断证据。 */
    public void setEvidence(List<String> evidence) {
        this.evidence = evidence;
    }
}
