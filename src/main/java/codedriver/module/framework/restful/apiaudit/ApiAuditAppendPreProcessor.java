/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.module.framework.restful.apiaudit;

import codedriver.framework.crossover.ICrossoverService;
import codedriver.framework.file.core.IEvent;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

@Component
public class ApiAuditAppendPreProcessor implements Consumer<IEvent>, ICrossoverService {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

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
