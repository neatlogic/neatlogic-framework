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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RootComponent
public class ElasticsearchDocumentFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IElasticsearchDocument> componentMap = new HashMap<>();
    private static final List<IElasticsearchDocument> components = new ArrayList<>();
    private static final Map<String, Object> lockMap = new ConcurrentHashMap<>();

    public static IElasticsearchDocument getIndex(String name) {
        IElasticsearchDocument index = componentMap.get(name);
        if (index == null) {
            throw new ElasticSearchIndexNotFoundException(name);
        }
        Object lock = lockMap.computeIfAbsent(name, key -> new Object());
        synchronized (lock) {
            String indexName = index.getIndexName(name);
            if (!index.isIndexExists(indexName)) {
                index.createIndex(indexName);
            }
        }
        return index;
    }

    public static List<IElasticsearchDocument> getAllIndex() {
        return Collections.unmodifiableList(components);
    }


    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IElasticsearchDocument> map = context.getBeansOfType(IElasticsearchDocument.class);
        for (Map.Entry<String, IElasticsearchDocument> entry : map.entrySet()) {
            componentMap.put(entry.getValue().getName(), entry.getValue());
            components.add(entry.getValue());
        }
    }

    @Override
    protected void myInit() {

    }
}
