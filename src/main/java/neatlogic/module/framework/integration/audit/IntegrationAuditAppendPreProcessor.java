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

package neatlogic.module.framework.integration.audit;

import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.crossover.ICrossoverService;
import neatlogic.framework.file.core.IEvent;
import neatlogic.framework.util.TimeUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Component
public class IntegrationAuditAppendPreProcessor implements Consumer<IEvent>, ICrossoverService {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TimeUtil.YYYY_MM_DD_HH_MM_SS_SSS);

    @Override
    public void accept(IEvent event) {
        JSONObject data = event.getData();
        String param = data.getString("param");
        String result = data.getString("result");
        String error = data.getString("error");
        /*
          组装文件内容JSON并且计算文件中每一块内容的开始坐标和偏移量
          例如参数的开始坐标为"param>>>>>>>>>"的字节数
          偏移量为param的字节数(注意一定要用UTF-8格式，否则计算出来的偏移量不对)
         */
        StringBuilder sb = new StringBuilder();
        sb.append(event.getName());
        sb.append(" ");
        Instant instant = Instant.ofEpochMilli(event.getTimeStamp());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        sb.append(localDateTime.format(dateTimeFormatter));
        sb.append(" ");
        sb.append(TenantContext.get().getTenantUuid());
        sb.append("\n");
        if (StringUtils.isNotBlank(param)) {
            sb.append("param>>>>>>>>>");
            sb.append("\n");
            sb.append(param);
            sb.append("\n");
            sb.append("param<<<<<<<<<");
            sb.append("\n");
        }
        if (StringUtils.isNotBlank(result)) {
            sb.append("result>>>>>>>>");
            sb.append("\n");
            sb.append(result);
            sb.append("\n");
            sb.append("result<<<<<<<<");
            sb.append("\n");
        }
        if (StringUtils.isNotBlank(error)) {
            sb.append("error>>>>>>>>>");
            sb.append("\n");
            sb.append(error);
            sb.append("\n");
            sb.append("error<<<<<<<<<");
        }
        event.setMessage(sb.toString());
    }
}
