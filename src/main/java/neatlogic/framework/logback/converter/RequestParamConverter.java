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
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

public class RequestParamConverter extends ClassicConverter implements Serializable {

    @Override
    public String convert(ILoggingEvent event) {
        Map<String, String> map = event.getMDCPropertyMap();
        String param = map.get("param");
        if (StringUtils.isNotBlank(param)) {
            return param;
        }
        return StringUtils.EMPTY;
    }
}
