/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.restful.apiaudit;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.crossover.ICrossoverService;
import codedriver.framework.file.core.IEvent;
import codedriver.framework.restful.dao.mapper.ApiAuditMapper;
import codedriver.framework.restful.dto.ApiAuditVo;
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
