/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.datawarehouse.enums;

public enum DatabaseVersion {
    MYSQL8("MySql8.x", "文本框");

    private final String name;
    private final String driver;

    DatabaseVersion(String _name, String _driver) {
        this.name = _name;
        this.driver = _driver;
    }

    public String getName() {
        return name;
    }

    public String getDriver() {
        return driver;
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
