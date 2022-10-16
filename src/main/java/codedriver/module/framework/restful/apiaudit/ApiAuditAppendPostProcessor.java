/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.restful.apiaudit;

import codedriver.framework.common.config.Config;
import codedriver.framework.crossover.ICrossoverService;
import codedriver.framework.file.core.IEvent;
import codedriver.framework.restful.dao.mapper.ApiAuditMapper;
import codedriver.framework.restful.dto.ApiAuditPathVo;
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
        if (event.isRollover()) {
            apiAuditMapper.updateApiAuditPathArchiveIndexIncrement();
        }
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

        long fileSize = event.getBeforeAppendFileSize();
        String finalMessage = event.getFinalMessage();
        String param = data.getString("param");
        String result = data.getString("result");
        String error = data.getString("error");
        if (StringUtils.isNotBlank(param)) {
            int index = finalMessage.indexOf(param);
            int paramStartIndex = finalMessage.substring(0, index).getBytes(StandardCharsets.UTF_8).length;
            int paramOffset = param.getBytes(StandardCharsets.UTF_8).length;
            ApiAuditPathVo apiAuditPathVo = new ApiAuditPathVo();
            apiAuditPathVo.setServerId(Config.SCHEDULE_SERVER_ID);
            apiAuditPathVo.setPath(path);
            apiAuditPathVo.setStartIndex(fileSize + paramStartIndex);
            apiAuditPathVo.setOffset(paramOffset);
            apiAuditMapper.insertApiAuditPath(apiAuditPathVo);
            apiAuditVo.setParamPathId(apiAuditPathVo.getId());
        }
        if (StringUtils.isNotBlank(result)) {
            int index = finalMessage.indexOf(result);
            int resultStartIndex = finalMessage.substring(0, index).getBytes(StandardCharsets.UTF_8).length;
            int resultOffset = result.getBytes(StandardCharsets.UTF_8).length;
            ApiAuditPathVo apiAuditPathVo = new ApiAuditPathVo();
            apiAuditPathVo.setServerId(Config.SCHEDULE_SERVER_ID);
            apiAuditPathVo.setPath(path);
            apiAuditPathVo.setStartIndex(fileSize + resultStartIndex);
            apiAuditPathVo.setOffset(resultOffset);
            apiAuditMapper.insertApiAuditPath(apiAuditPathVo);
            apiAuditVo.setResultPathId(apiAuditPathVo.getId());
        }
        if (StringUtils.isNotBlank(error)) {
            int index = finalMessage.indexOf(error);
            int errorStartIndex = finalMessage.substring(0, index).getBytes(StandardCharsets.UTF_8).length;
            int errorOffset = error.getBytes(StandardCharsets.UTF_8).length;
            ApiAuditPathVo apiAuditPathVo = new ApiAuditPathVo();
            apiAuditPathVo.setServerId(Config.SCHEDULE_SERVER_ID);
            apiAuditPathVo.setPath(path);
            apiAuditPathVo.setStartIndex(fileSize + errorStartIndex);
            apiAuditPathVo.setOffset(errorOffset);
            apiAuditMapper.insertApiAuditPath(apiAuditPathVo);
            apiAuditVo.setErrorPathId(apiAuditPathVo.getId());
            apiAuditVo.setStatus(ApiAuditVo.FAILED);
        } else {
            apiAuditVo.setStatus(ApiAuditVo.SUCCEED);
        }
        apiAuditMapper.insertApiAudit(apiAuditVo);
    }
}
