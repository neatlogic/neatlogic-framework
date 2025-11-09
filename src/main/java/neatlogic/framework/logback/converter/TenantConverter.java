/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;

/**
 * logback.xml文件使用，日志中输出租户信息
 * @author linbq
 * @since 2022/1/19 15:12
 **/
public class TenantConverter extends ClassicConverter implements Serializable {

    /**
     * The convert method is responsible for extracting data from the event and
     * storing it for later use by the write method.
     *
     * @param event
     */
    @Override
    public String convert(ILoggingEvent event) {
        Map<String, String> map = event.getMDCPropertyMap();
        String tenant = map.get("tenant");
        if (StringUtils.isNotBlank(tenant)) {
            String userId = map.get("userId");
            if (StringUtils.isNotBlank(userId)) {
                tenant += "(" + userId + ")";
            }
            return tenant;
        }
        return StringUtils.EMPTY;
    }

}
