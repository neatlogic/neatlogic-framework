/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.portal.widget.core;

import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.module.ModuleVo;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PortalWidgetFactory {

    private static final Logger logger = LoggerFactory.getLogger(PortalWidgetFactory.class);
    private static final Set<IPortalWidget> set = new HashSet<>();

    private static final Map<String, IPortalWidget> map = new HashMap<>();

    static {
        Reflections reflections = new Reflections("neatlogic");
        Set<Class<? extends IPortalWidget>> classSet = reflections.getSubTypesOf(IPortalWidget.class);
        for (Class<? extends IPortalWidget> c : classSet) {
//            Collections.addAll(set, c.getEnumConstants());
            IPortalWidget[] enumConstants = c.getEnumConstants();
            for (IPortalWidget portalWidget : enumConstants) {
                System.out.println("portalWidget = " + portalWidget.getClass().getName());
                if (map.containsKey(portalWidget.getValue())) {
                    logger.error("门户小部件 '" + portalWidget.getClass().getSimpleName() + "(" + portalWidget.getValue() + ")' 重复了");
                    System.exit(1);
                }
                set.add(portalWidget);
                map.put(portalWidget.getValue(), portalWidget);
            }
        }
    }

//    public static IPortalWidget getPortalWidget(String value) {
//        for (IPortalWidget portalWidget : set) {
//            if (Objects.equals(portalWidget.getValue(), value)) {
//                return portalWidget;
//            }
//        }
//        return null;
//    }

    public static IPortalWidget getPortalWidget(String name) {
        return map.get(name);
    }

    public static List<IPortalWidget> getPortalWidgetListByModuleGroup(String moduleGroup) {
        List<IPortalWidget> result = new ArrayList<>();
        if (StringUtils.isBlank(moduleGroup)) {
            result.addAll(set);
        } else {
            for (IPortalWidget portalWidget : set) {
                String className = portalWidget.getClass().getName();
                if ("framework".equals(moduleGroup) && className.startsWith("neatlogic.framework.")) {
                    result.add(portalWidget);
                    continue;
                }
                for (ModuleVo moduleVo : ModuleUtil.getAllModuleList()) {
                    if (moduleGroup.equals(moduleVo.getGroup())
                            && className.startsWith("neatlogic.module." + moduleVo.getId() + ".")) {
                        result.add(portalWidget);
                        break;
                    }
                }
            }
        }
        result.sort(Comparator.comparing(IPortalWidget::getValue));
        return result;
    }
}
