package codedriver.framework.file.dto;

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
//        int startIndex = 0;
//        int offset = 0;
//        int serverId = 0;
        String startIndexStr = paramMap.get("startIndex");
        if (StringUtils.isNotBlank(startIndexStr)) {
            startIndex = Integer.parseInt(startIndexStr);
        } else {
            startIndex = null;
        }
        String offsetStr = paramMap.get("offset");
        if (StringUtils.isNotBlank(offsetStr)) {
            offset = Integer.parseInt(offsetStr);
        } else {
            offset = null;
        }
        String serverIdStr = paramMap.get("serverId");
        if (StringUtils.isNotBlank(serverIdStr)) {
            serverId = Integer.parseInt(serverIdStr);
        } else {
            serverId = null;
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
