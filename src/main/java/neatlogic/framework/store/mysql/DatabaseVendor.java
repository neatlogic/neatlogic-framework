package neatlogic.framework.store.mysql;

public enum DatabaseVendor {
    MYSQL("MySQL", "mysql"),
    TIDB("TiDB", "tidb"),
    OCEAN_BASE("OceanBase", "oceanbase")
    ;

    String name;
    String alias;

    DatabaseVendor(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }
}
