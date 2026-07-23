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

package neatlogic.framework.dto.license;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 兼容许可证日期既可能是毫秒值，也可能是 yyyy-MM-dd 字符串的历史格式。
 */
public class LicenseDateDeserializer implements ObjectDeserializer {

    @Override
    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object value = parser.parse();
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return (T) Long.valueOf(((Number) value).longValue());
        }
        String dateValue = String.valueOf(value);
        if (StringUtils.isBlank(dateValue)) {
            return null;
        }
        if (StringUtils.isNumeric(dateValue)) {
            return (T) Long.valueOf(dateValue);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            return (T) Long.valueOf(dateFormat.parse(dateValue).getTime());
        } catch (ParseException e) {
            throw new JSONException("license date format is invalid: " + dateValue, e);
        }
    }

    @Override
    public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}
