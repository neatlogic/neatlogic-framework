/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
            subscribeHandlerVoList.add(new SubscribeHandlerVo(component.getName(), component.getClassName()));
        }
    }

    @Override
    protected void myInit() {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
