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
