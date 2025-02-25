/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
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
