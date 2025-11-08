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

package neatlogic.framework.datawarehouse.formater.core;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataSourceFieldFormaterFactory {
    private static final Map<String, IDatasourceFieldFormater> formaterMap = new HashMap<>();

    static {
        Reflections reflections = new Reflections("neatlogic");
        Set<Class<? extends IDatasourceFieldFormater>> modules = reflections.getSubTypesOf(IDatasourceFieldFormater.class);
        for (Class<? extends IDatasourceFieldFormater> c : modules) {
            IDatasourceFieldFormater handler;
            try {
                handler = c.newInstance();
                if (StringUtils.isNotBlank(handler.getType())) {
                    formaterMap.put(handler.getType(), handler);
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static IDatasourceFieldFormater getFormater(String type) {
        return formaterMap.get(type);
    }
}
