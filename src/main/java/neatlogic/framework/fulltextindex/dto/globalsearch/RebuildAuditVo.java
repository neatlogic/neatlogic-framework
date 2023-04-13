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

package neatlogic.framework.fulltextindex.dto.globalsearch;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;
import org.apache.commons.lang3.StringUtils;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;

public class RebuildAuditVo {
    public enum Status {
        DOING("doing", new I18n("enum.framework.rebuildauditvo.status.doing")),
        DONE("done", new I18n("enum.framework.rebuildauditvo.status.done"));

        private String value;
        private I18n name;

        private Status(String _value, I18n _name) {
            this.value = _value;
            this.name = _name;
        }

        public String getValue() {
            return value;
        }

        public String getName() {
            return I18nUtils.getMessage(name.toString());
        }

        public static String getName(String v) {
            for (Status s : Status.values()) {
                if (s.getValue().equals(v)) {
                    return s.getName();
                }
            }
            return "";
        }
    }

    private String type;
    private String startTime;
    private String endTime;
    private String status;
    private String statusText;
    private String editor;
    private String editorName;
    private Integer serverId;

    public RebuildAuditVo() {

    }

    public RebuildAuditVo(String _type, String _status) {
        type = _type;
        status = _status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusText() {
        if (StringUtils.isNotBlank(status)) {
            statusText = Status.getName(status);
        }
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getEditor() {
        if (StringUtils.isBlank(editor)) {
            editor = UserContext.get().getUserUuid();
        }
        return editor;
    }

    public void setEditor(String editor) {
        this.editor = editor;
    }

    public String getEditorName() {
        return editorName;
    }

    public void setEditorName(String editorName) {
        this.editorName = editorName;
    }

    public Integer getServerId() {
        if (serverId == null) {
            serverId = Config.SCHEDULE_SERVER_ID;
        }
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

}
