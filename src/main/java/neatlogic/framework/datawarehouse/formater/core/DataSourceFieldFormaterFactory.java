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
