/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package neatlogic.framework.form.constvalue;

import java.util.Objects;

public enum DateRange {
    LAST_DAY(1, "day", "最近一天"),
    LAST_WEEK(1, "week", "最近一周"),
    LAST_HALF_MONTH(15, "day", "最近半个月"),
    LAST_MONTH(1, "month", "最近一个月"),
    LAST_HALF_YEAR(6, "month", "最近半年"),
    LAST_YEAR(1, "year", "最近一年")
    ;
    Integer timeRange;
    String timeUnit;
    String text;

    DateRange(Integer timeRange, String timeUnit, String text) {
        this.timeRange = timeRange;
        this.timeUnit = timeUnit;
        this.text = text;
    }

    public Integer getTimeRange() {
        return timeRange;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public String getText() {
        return text;
    }

    public static String getText(Integer timeRange, String timeUnit) {
        for (DateRange e : values()) {
            if (Objects.equals(timeUnit, e.timeUnit) && Objects.equals(timeRange, e.timeRange)) {
                return e.text;
            }
        }
        return "";
    }
}
