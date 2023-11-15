package neatlogic.framework.store.mysql;

public enum DatabaseVendor {
    MYSQL("MySQL", "mysql"),
    TIDB("TiDB", "tidb"),
    OCEAN_BASE("OceanBase", "oceanbase")
    ;

    String name;
    String databaseId;

    DatabaseVendor(String name, String databaseId) {
        this.name = name;
        this.databaseId = databaseId;
    }

    public String getName() {
        return name;
    }

    public String getDatabaseId() {
        return databaseId;
    }
}
