/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.util.javascript;

import jdk.nashorn.api.scripting.ClassFilter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScriptClassFilter implements ClassFilter {
    private final List<String> classPrefixList = new ArrayList<>();

    public ScriptClassFilter(String... classPrefixName) {
        if (classPrefixName != null && classPrefixName.length > 0) {
            Collections.addAll(classPrefixList, classPrefixName);
        }
    }

    @Override
    public boolean exposeToScripts(String s) {
        if (CollectionUtils.isNotEmpty(classPrefixList)) {
            for (String prefix : classPrefixList) {
                if (s.toLowerCase().startsWith(prefix.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}
