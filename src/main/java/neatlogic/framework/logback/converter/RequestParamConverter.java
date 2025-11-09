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
