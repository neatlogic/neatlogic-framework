/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.sqlgenerator;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class FunctionVo {
    private final String name;
    private Boolean distinct = false;

    private final Object[] parameters;

    FunctionVo(String name, Object ... parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public FunctionVo withDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        String result = StringUtils.EMPTY;
        if (this.name != null) {
            result += this.name.trim();
            result += "(";
            if (this.parameters != null) {
                List<String> paramterList = new ArrayList<>();
                for (Object parameter : this.parameters) {
                    if (parameter != null) {
                        paramterList.add(parameter.toString());
                    }
                }
                result += String.join(", ", paramterList);
            }
            result += ")";
        }
        return result;
    }
}
