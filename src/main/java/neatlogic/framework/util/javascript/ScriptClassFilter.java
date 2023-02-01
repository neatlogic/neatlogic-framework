/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.util.javascript;

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
