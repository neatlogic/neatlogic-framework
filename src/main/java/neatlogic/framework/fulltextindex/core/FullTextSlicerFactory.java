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

package neatlogic.framework.fulltextindex.core;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class FullTextSlicerFactory {
    private static final Logger logger = LoggerFactory.getLogger(FullTextSlicerFactory.class);
    private static final Map<String, IFullTextSlicer> slicerMap = new HashMap<>();
    private static final List<IFullTextSlicer> slicerList = new ArrayList<>();

    static {
        Reflections reflections = new Reflections("neatlogic");
        Set<Class<? extends IFullTextSlicer>> modules = reflections.getSubTypesOf(IFullTextSlicer.class);
        for (Class<? extends IFullTextSlicer> c : modules) {
            IFullTextSlicer slicer;
            try {
                slicer = c.newInstance();
                if (StringUtils.isNotBlank(slicer.getType())) {
                    slicerMap.put(slicer.getType(), slicer);
                    slicerList.add(slicer);
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public static IFullTextSlicer getSlicer(String type) {
        return slicerMap.get(type);
    }

    public static List<IFullTextSlicer> getSlicerList() {
        return slicerList;
    }
}
