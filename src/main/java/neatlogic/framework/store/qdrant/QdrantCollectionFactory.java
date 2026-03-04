/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the Sustainable Use License (SUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.store.qdrant;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class QdrantCollectionFactory extends ModuleInitializedListenerBase {
    private static final Map<String, IQdrantCollection> collectionMap = new HashMap<>();
    private static final List<IQdrantCollection> collectionList = new ArrayList<>();

    public static IQdrantCollection getCollection(String name) {
        return collectionMap.get(name.toLowerCase());
    }

    public static List<IQdrantCollection> getAllCollection() {
        return collectionList;
    }


    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IQdrantCollection> myMap = context.getBeansOfType(IQdrantCollection.class);
        for (Map.Entry<String, IQdrantCollection> entry : myMap.entrySet()) {
            IQdrantCollection handler = entry.getValue();
            collectionMap.put(handler.getName().toLowerCase(), handler);
            collectionList.add(handler);
        }
    }

    @Override
    protected void myInit() {

    }
}
