package neatlogic.framework.common.util;

import neatlogic.framework.store.mysql.DatasourceManager;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class TenantUtil {
    private static final Set<String> tenantSet = new HashSet<>();

    public static boolean hasTenant(String tenant) {
        if (StringUtils.isNotBlank(tenant)) {
            return tenantSet.contains(tenant);
        }
        return false;
    }

    public static void addTenant(String tenant) {
        if (StringUtils.isNotBlank(tenant)) {
            tenantSet.add(tenant);
        }
    }

    /**
     * 从内存租户集合移除租户，并卸载该租户相关数据源。
     * 数据源需要交给数据源管理器从路由映射中移除并关闭，不能只关闭连接池。
     *
     * @param tenant 租户uuid
     */
    public static void removeTenant(String tenant) {
        if (StringUtils.isNotBlank(tenant)) {
            tenantSet.remove(tenant);
            DatasourceManager.removeTenantDatasource(tenant);
        }
    }

}
