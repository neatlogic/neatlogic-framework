/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
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
