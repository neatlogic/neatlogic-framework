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

package neatlogic.framework.sqlgenerator;

import neatlogic.framework.util.TimeUtil;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ValueVo {

    private String strValue;
    private Integer intValue;
    private Long longValue;
    private Double doubleValue;
    private List<?> list;

    private Date date;

    ValueVo(String strValue) {
        this.strValue = strValue;
    }

    ValueVo(Integer intValue) {
        this.intValue = intValue;
    }

    ValueVo(Long longValue) {
        this.longValue = longValue;
    }

    ValueVo(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    ValueVo(List<?> list) {
        this.list = list;
    }

    public ValueVo(Date date) {
        this.date = date;
    }

    public String getStrValue() {
        return strValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public List<?> getList() {
        return list;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        if (strValue != null) {
            return "'" + strValue + "'";
        } else if (intValue != null) {
            return intValue.toString();
        } else if (longValue != null) {
            return longValue.toString();
        } else if (doubleValue != null) {
            return doubleValue.toString();
        } else if (date != null) {
            return "'" + (new SimpleDateFormat(TimeUtil.YYYY_MM_DD_HH_MM_SS)).format(date) + "'";
        } else if (list != null) {
            List<String> resultList = new ArrayList<>();
            for (Object obj : list) {
                if (obj != null) {
                    if (obj instanceof String) {
                        resultList.add("'" + obj + "'");
                    } else {
                        resultList.add(obj.toString());
                    }
                }
            }
            return String.join(", ", resultList);
        }
        return StringUtils.EMPTY;
    }
}
