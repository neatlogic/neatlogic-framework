/*
 * Copyright(c) 2022 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import codedriver.framework.asynchronization.threadlocal.RequestContext;

/**
 * logback.xml文件使用，日志中输出请求url
 * @author linbq
 * @since 2022/1/19 16:08
 **/
public class RequestUrlConverter extends ClassicConverter {
    /**
     * The convert method is responsible for extracting data from the event and
     * storing it for later use by the write method.
     *
     * @param event
     */
    @Override
    public String convert(ILoggingEvent event) {
        RequestContext requestContext = RequestContext.get();
        if (requestContext != null) {
            return requestContext.getUrl();
        }
        return "";
    }
}
