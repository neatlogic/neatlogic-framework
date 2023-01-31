package neatlogic.framework.fulltextindex.core;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * @Title: FullTextIndexModuleContainer
 * @Package: neatlogic.framework.fulltextindex.core
 * @Description: TODO
 * @author: chenqiwei
 * @date: 2021/3/15:14 下午
 * Copyright(c) 2021 TechSure Co.,Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public class FullTextIndexModuleContainer {
    private final static ThreadLocal<String> MODULE_ID = new ThreadLocal<>();

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
