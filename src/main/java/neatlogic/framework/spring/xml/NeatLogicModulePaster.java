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

package neatlogic.framework.spring.xml;

import neatlogic.framework.dto.module.ModuleVo;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

public class NeatLogicModulePaster extends AbstractSingleBeanDefinitionParser {
    @SuppressWarnings({"rawtypes"})
    protected Class getBeanClass(Element element) {
        return ModuleVo.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        String id = element.getAttribute("id");
        String name = element.getAttribute("name");
        String description = element.getAttribute("description");
        bean.addPropertyValue("id", id);
        bean.addPropertyValue("name", name);
        bean.addPropertyValue("description", description);
        bean.setLazyInit(true);
    }
}
