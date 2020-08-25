package codedriver.framework.common.util;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class TenantUtil {
	private static Set<String> tenantSet = new HashSet<>();

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
