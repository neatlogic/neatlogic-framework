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

package neatlogic.framework.notify.core;

import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class NotifyTriggerTypeFactory {
    /**
     * 标记是否未初始化数据，只初始化一次
     **/
    private static volatile boolean isUninitialized = true;

    private static final Set<INotifyTriggerType> set = new HashSet<>();

    public static Set<INotifyTriggerType> getNotifyTriggerTypeList() {
        if (isUninitialized) {
            synchronized (NotifyTriggerTypeFactory.class) {
                if (isUninitialized) {
                    Reflections reflections = new Reflections("neatlogic");
                    Set<Class<? extends INotifyTriggerType>> classSet = reflections.getSubTypesOf(INotifyTriggerType.class);
                    for (Class<? extends INotifyTriggerType> c : classSet) {
                        try {
                            set.addAll(Arrays.asList(c.getEnumConstants()));
                        } catch (Exception ignored) {

                        }
                    }
                    isUninitialized = false;
                }
            }
        }
        return set;
    }

    public static String getText(String value) {
        for (INotifyTriggerType triggerType : getNotifyTriggerTypeList()) {
            if (Objects.equals(triggerType.getTrigger(), value)) {
                return triggerType.getText();
            }
        }
        return StringUtils.EMPTY;
    }
}
