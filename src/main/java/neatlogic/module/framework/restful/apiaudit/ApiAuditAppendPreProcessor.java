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

package neatlogic.module.framework.restful.apiaudit;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.crossover.ICrossoverService;
import neatlogic.framework.file.core.IEvent;
import neatlogic.framework.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Component
public class ApiAuditAppendPreProcessor implements Consumer<IEvent>, ICrossoverService {

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
