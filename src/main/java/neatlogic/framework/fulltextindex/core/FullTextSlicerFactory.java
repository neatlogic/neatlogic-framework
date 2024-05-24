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

package neatlogic.framework.fulltextindex.core;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.*;

public class FullTextSlicerFactory {
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
                ex.printStackTrace();
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
