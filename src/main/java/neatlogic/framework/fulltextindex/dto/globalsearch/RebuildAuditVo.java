/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.fulltextindex.dto.globalsearch;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;
import org.apache.commons.lang3.StringUtils;

public class RebuildAuditVo {
    public enum Status {
        DOING("doing", new I18n("重建中")),
        DONE("done", new I18n("重建完毕"));

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
            return $.t(name.toString());
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
