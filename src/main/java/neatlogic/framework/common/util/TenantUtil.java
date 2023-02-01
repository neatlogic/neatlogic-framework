package neatlogic.framework.common.util;

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

	public static void removeTenant(String tenant) {
		if (StringUtils.isNotBlank(tenant)) {
			tenantSet.remove(tenant);
		}
	}

}
