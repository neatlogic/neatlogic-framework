/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
