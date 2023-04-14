/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. 
 */

package neatlogic.framework.form.constvalue;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

import java.util.Objects;

public enum DateRange {
    LAST_DAY(1, "day", new I18n("enum.framework.daterange.last_day")),
    LAST_WEEK(1, "week", new I18n("enum.framework.daterange.last_week")),
    LAST_HALF_MONTH(15, "day", new I18n("enum.framework.daterange.last_half_month")),
    LAST_MONTH(1, "month", new I18n("enum.framework.daterange.last_month")),
    LAST_HALF_YEAR(6, "month", new I18n("enum.framework.daterange.last_half_year")),
    LAST_YEAR(1, "year", new I18n("enum.framework.daterange.last_year"));
    Integer timeRange;
    String timeUnit;
    I18n text;

    DateRange(Integer timeRange, String timeUnit, I18n text) {
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
        return I18nUtils.getMessage(text.toString());
    }

    public static String getText(Integer timeRange, String timeUnit) {
        for (DateRange e : values()) {
            if (Objects.equals(timeUnit, e.timeUnit) && Objects.equals(timeRange, e.timeRange)) {
                return e.getText();
            }
        }
        return "";
    }
}
