/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.form.constvalue;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

import java.util.Objects;

public enum DateRange {
    LAST_DAY(1, "day", new I18n("最近一天")),
    LAST_WEEK(1, "week", new I18n("最近一周")),
    LAST_HALF_MONTH(15, "day", new I18n("最近半个月")),
    LAST_MONTH(1, "month", new I18n("最近一个月")),
    LAST_HALF_YEAR(6, "month", new I18n("最近半年")),
    LAST_YEAR(1, "year", new I18n("最近一年"));
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
        return $.t(text.toString());
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
