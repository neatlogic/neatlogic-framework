/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.lcs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import neatlogic.framework.lcs.linehandler.core.ILineHandler;
import neatlogic.framework.lcs.linehandler.core.LineHandlerFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class BaseLineVo {
    private Integer lineNumber;
    private String changeType;
    private String content;
    private String handler;
    private JSONObject config;
    @JSONField(serialize = false)
    private String configStr;

    public BaseLineVo() {

    }

    public BaseLineVo(Integer lineNumber, String handler, String content) {
        this.lineNumber = lineNumber;
        this.content = content;
        this.handler = handler;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(Integer lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = JSON.parseObject(config);
    }

    public String getConfigStr() {
        if(StringUtils.isBlank(configStr) && config != null) {
            configStr = config.toJSONString();
        }
        return configStr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((handler == null) ? 0 : handler.hashCode());
        String mainBody = null;
        ILineHandler lineHandler = LineHandlerFactory.getHandler(handler);
        if (lineHandler != null) {
            mainBody = lineHandler.getMainBody(this);
        }
        result = prime * result + ((mainBody == null) ? 0 : mainBody.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseLineVo other = (BaseLineVo)obj;
        if (handler == null) {
            if (other.handler != null)
                return false;
        } else if (!handler.equals(other.handler))
            return false;
        ILineHandler lineHandler = LineHandlerFactory.getHandler(handler);
        if (lineHandler == null) {
            return true;
        }
        return Objects.equals(lineHandler.getMainBody(this), lineHandler.getMainBody(other));
    }
}
