package neatlogic.framework.fulltextindex.core;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * @Title: FullTextIndexModuleContainer
 * @Package: neatlogic.framework.fulltextindex.core
 * @date: 2021/3/15:14 下午
 **/
public class FullTextIndexModuleContainer {
    private static final ThreadLocal<String> MODULE_ID = new ThreadLocal<>();

    public static void set(String moduleId) {
        if (StringUtils.isNotBlank(moduleId)) {
            MODULE_ID.set(moduleId);
        }
    }

    public static String get() {
        String s = MODULE_ID.get();
        if (StringUtils.isNotBlank(s)) {
            s = s.toLowerCase(Locale.ROOT);
        }
        return s;
    }

    public static void remove() {
        MODULE_ID.remove();
    }
}
