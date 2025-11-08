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

package neatlogic.framework.mq.core;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.mq.dto.SubscribeHandlerVo;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RootComponent
public class SubscribeHandlerFactory extends ModuleInitializedListenerBase implements BeanFactoryPostProcessor {
    private static final Map<String, ISubscribeHandler> componentMap = new HashMap<>();

    public static ISubscribeHandler getHandler(String handlerId) {
        return componentMap.get(handlerId);
    }

    private static final List<SubscribeHandlerVo> subscribeHandlerVoList = new ArrayList<>();

    public static List<SubscribeHandlerVo> getSubscribeHandlerList() {
        return subscribeHandlerVoList;
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, ISubscribeHandler> myMap = context.getBeansOfType(ISubscribeHandler.class);
        for (Map.Entry<String, ISubscribeHandler> entry : myMap.entrySet()) {
            ISubscribeHandler component = entry.getValue();
            componentMap.put(component.getClassName(), component);
            subscribeHandlerVoList.add(new SubscribeHandlerVo(component.getName(), component.getLabel(), component.getClassName()));
        }
    }

    @Override
    protected void myInit() {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
