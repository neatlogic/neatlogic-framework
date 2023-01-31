/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
