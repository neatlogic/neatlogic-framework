/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.datawarehouse.enums;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

public enum DatabaseVersion {
    MYSQL8("MySql8.x", new I18n("文本框"));

    private final String name;
    private final I18n driver;

    DatabaseVersion(String _name, I18n _driver) {
        this.name = _name;
        this.driver = _driver;
    }

    public String getName() {
        return name;
    }

    public String getDriver() {
        return $.t(driver.toString());
    }


    public static DatabaseVersion getVersion(String name) {
        for (DatabaseVersion s : DatabaseVersion.values()) {
            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }
}
