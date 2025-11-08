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
