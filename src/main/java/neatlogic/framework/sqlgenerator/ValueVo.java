/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
