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

package neatlogic.framework.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Map;

/**
 * logback.xml文件使用，日志中输出请求url
 * @author linbq
 * @since 2022/1/19 16:08
 **/
public class RequestUrlConverter extends ClassicConverter implements Serializable {

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
            String url = requestContext.getUrl();
            if (url == null) {
                url = StringUtils.EMPTY;
            }
            HttpServletRequest request = requestContext.getRequest();
            if (request != null) {
                String remoteAddr = request.getRemoteAddr();
                if (StringUtils.isNotBlank(remoteAddr)) {
                    url += "(" + remoteAddr + ")";
                }
            }
            return url;
        } else {
            Map<String, String> map = event.getMDCPropertyMap();
            String url = map.get("url");
            if (StringUtils.isNotBlank(url)) {
                return url;
            }
        }
        return StringUtils.EMPTY;
    }
}
