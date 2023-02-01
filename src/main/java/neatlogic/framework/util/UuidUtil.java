package neatlogic.framework.util;

import java.util.UUID;

public class UuidUtil {
	public static String randomUuid() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
