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
