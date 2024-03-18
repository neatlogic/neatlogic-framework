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

package neatlogic.framework.file.dto;

import neatlogic.framework.exception.file.FilePathIllegalException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class AuditFilePathVo {

    private final String path;
    private final Integer startIndex;
    private final Integer offset;
    private final Integer serverId;
    public AuditFilePathVo(String filePath) {
        Map<String, String> paramMap = new HashMap<>();
        String[] split = filePath.split("\\?");
        path = split[0];
        if (split.length >= 2) {
            String[] paramArray = split[1].split("&");
            for (String param : paramArray) {
                if (param.contains("=")) {
                    String[] paramKeyValue = param.split("=");
                    paramMap.put(paramKeyValue[0], paramKeyValue[1]);
                }
            }
        }
        String startIndexStr = paramMap.get("startIndex");
        if (StringUtils.isNotBlank(startIndexStr)) {
            startIndex = Integer.parseInt(startIndexStr);
        } else {
            throw new FilePathIllegalException(filePath);
        }
        String offsetStr = paramMap.get("offset");
        if (StringUtils.isNotBlank(offsetStr)) {
            offset = Integer.parseInt(offsetStr);
        } else {
            throw new FilePathIllegalException(filePath);
        }
        String serverIdStr = paramMap.get("serverId");
        if (StringUtils.isNotBlank(serverIdStr)) {
            serverId = Integer.parseInt(serverIdStr);
        } else {
            throw new FilePathIllegalException(filePath);
        }
    }

    public String getPath() {
        return path;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public Integer getOffset() {
        return offset;
    }

    public Integer getServerId() {
        return serverId;
    }
}
