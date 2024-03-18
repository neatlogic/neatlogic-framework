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
import neatlogic.framework.mq.dto.TopicVo;

import java.util.*;

@RootComponent
public class TopicFactory extends ModuleInitializedListenerBase {
    private static final Map<String, ITopic> componentMap = new HashMap<>();
    private static final List<TopicVo> topicList = new ArrayList<>();

    public static ITopic getTopic(String topicName) {
        return componentMap.get(topicName);
    }

    public static List<TopicVo> getTopicList() {
        return topicList;
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, ITopic> myMap = context.getBeansOfType(ITopic.class);
        for (Map.Entry<String, ITopic> entry : myMap.entrySet()) {
            ITopic component = entry.getValue();
            if (component.getName() != null) {
                componentMap.put(component.getName(), component);
                TopicVo topicVo = new TopicVo();
                topicVo.setName(component.getName());
                topicVo.setLabel(component.getLabel());
                topicVo.setDescription(component.getDescription());
                topicList.add(topicVo);
            }
        }
        topicList.sort(Comparator.comparing(TopicVo::getName));
    }

    @Override
    protected void myInit() {

    }
}
