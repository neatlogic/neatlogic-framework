/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.store.elasticsearch;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.exception.elasticsearch.ElasticSearchIndexNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class ElasticsearchIndexFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IElasticsearchIndex> componentMap = new HashMap<>();
    private static final List<IElasticsearchIndex> components = new ArrayList<>();

    public static IElasticsearchIndex getIndex(String name) {
        IElasticsearchIndex index = componentMap.get(name);
        if (index == null) {
            throw new ElasticSearchIndexNotFoundException(name);
        }
        return index;
    }

    public static List<IElasticsearchIndex> getAllIndex() {
        return components;
    }


    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IElasticsearchIndex> map = context.getBeansOfType(IElasticsearchIndex.class);
        for (Map.Entry<String, IElasticsearchIndex> entry : map.entrySet()) {
            componentMap.put(entry.getValue().getName(), entry.getValue());
            components.add(entry.getValue());
        }
    }

    @Override
    protected void myInit() {

    }
}
