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

package neatlogic.module.framework.integration.audit;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.crossover.ICrossoverService;
import neatlogic.framework.file.core.IEvent;
import neatlogic.framework.integration.dao.mapper.IntegrationMapper;
import neatlogic.framework.integration.dto.IntegrationAuditVo;
import neatlogic.framework.restful.dto.ApiAuditVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Component
public class IntegrationAuditAppendPostProcessor implements Consumer<IEvent>, ICrossoverService {
    @Resource
    private IntegrationMapper integrationMapper;

    @Override
    public void accept(IEvent event) {
        JSONObject data = event.getData();
        String path = data.getString("path");
        String dataHome = Config.DATA_HOME() + TenantContext.get().getTenantUuid();
        File file = new File(dataHome);
        dataHome = file.getPath();
        if (path.startsWith(dataHome)) {
            path = "${home}" + path.substring(dataHome.length());
        }
        JSONObject integrationAuditObj = data.getJSONObject("integrationAudit");
        IntegrationAuditVo integrationAuditVo = integrationAuditObj.toJavaObject(IntegrationAuditVo.class);
        integrationAuditVo.setServerId(Config.SCHEDULE_SERVER_ID);
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
            integrationAuditVo.setParamFilePath(filePath);
        }
        if (StringUtils.isNotBlank(result)) {
            int index = message.indexOf(result);
            int startIndex = message.substring(0, index).getBytes(StandardCharsets.UTF_8).length;
            int offset = result.getBytes(StandardCharsets.UTF_8).length;
            String filePath = path + "?startIndex=" + (fileSize + startIndex) + "&offset=" + offset + "&serverId=" + Config.SCHEDULE_SERVER_ID;
            integrationAuditVo.setResultFilePath(filePath);
        }
        if (StringUtils.isNotBlank(error)) {
            int index = message.indexOf(error);
            int startIndex = message.substring(0, index).getBytes(StandardCharsets.UTF_8).length;
            int offset = error.getBytes(StandardCharsets.UTF_8).length;
            String filePath = path + "?startIndex=" + (fileSize + startIndex) + "&offset=" + offset + "&serverId=" + Config.SCHEDULE_SERVER_ID;
            integrationAuditVo.setErrorFilePath(filePath);
            integrationAuditVo.setStatus(ApiAuditVo.FAILED);
        } else {
            integrationAuditVo.setStatus(ApiAuditVo.SUCCEED);
        }
        integrationMapper.insertIntegrationAudit(integrationAuditVo);
    }
}
