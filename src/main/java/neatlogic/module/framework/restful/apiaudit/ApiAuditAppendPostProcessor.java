/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.module.framework.restful.apiaudit;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.crossover.ICrossoverService;
import neatlogic.framework.file.core.IEvent;
import neatlogic.framework.restful.dao.mapper.ApiAuditMapper;
import neatlogic.framework.restful.dto.ApiAuditVo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Consumer;

@Component
public class ApiAuditAppendPostProcessor implements Consumer<IEvent>, ICrossoverService {
    @Resource
    private ApiAuditMapper apiAuditMapper;

    @Override
    public void accept(IEvent event) {
        JSONObject data = event.getData();
        ApiAuditVo apiAuditVo = new ApiAuditVo();
        apiAuditVo.setToken(data.getString("token"));
        apiAuditVo.setUserUuid(data.getString("userUuid"));
        apiAuditVo.setAuthtype(data.getString("authtype"));
        apiAuditVo.setIp(data.getString("ip"));
        apiAuditVo.setServerId(Config.SCHEDULE_SERVER_ID);
        long startTime = data.getLongValue("startTime");
        long endTime = data.getLongValue("endTime");
        apiAuditVo.setStartTime(new Date(startTime));
        apiAuditVo.setEndTime(new Date(endTime));
        apiAuditVo.setTimeCost(endTime - startTime);

        String path = data.getString("path");
        String dataHome = Config.DATA_HOME() + TenantContext.get().getTenantUuid();
        if (path.startsWith(dataHome)) {
            path = "${home}" + path.substring(dataHome.length());
        }
        long fileSize = event.getBeforeAppendFileSize();
        String message = event.getFormattedMessage();
        String param = data.getString("param");
        String result = data.getString("result");
        String error = data.getString("error");
        if (StringUtils.isNotBlank(param)) {
            int index = message.indexOf(param);
            int startIndex = message.substring(0, index).getBytes(StandardCharsets.UTF_8).length;
            int offset = param.getBytes(StandardCharsets.UTF_8).length;
            String filePath = path + "?startIndex=" + (fileSize + startIndex) + "&offset=" + offset + "&serverId=" + Config.SCHEDULE_SERVER_ID;
            apiAuditVo.setParamFilePath(filePath);
        }
        if (StringUtils.isNotBlank(result)) {
            int index = message.indexOf(result);
            int startIndex = message.substring(0, index).getBytes(StandardCharsets.UTF_8).length;
            int offset = result.getBytes(StandardCharsets.UTF_8).length;
            String filePath = path + "?startIndex=" + (fileSize + startIndex) + "&offset=" + offset + "&serverId=" + Config.SCHEDULE_SERVER_ID;
            apiAuditVo.setResultFilePath(filePath);
        }
        if (StringUtils.isNotBlank(error)) {
            int index = message.indexOf(error);
            int startIndex = message.substring(0, index).getBytes(StandardCharsets.UTF_8).length;
            int offset = error.getBytes(StandardCharsets.UTF_8).length;
            String filePath = path + "?startIndex=" + (fileSize + startIndex) + "&offset=" + offset + "&serverId=" + Config.SCHEDULE_SERVER_ID;
            apiAuditVo.setErrorFilePath(filePath);
            apiAuditVo.setStatus(ApiAuditVo.FAILED);
        } else {
            apiAuditVo.setStatus(ApiAuditVo.SUCCEED);
        }
        apiAuditMapper.insertApiAudit(apiAuditVo);
    }
}
