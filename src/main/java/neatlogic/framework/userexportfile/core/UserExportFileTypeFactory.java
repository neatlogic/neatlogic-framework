/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.userexportfile.core;

import org.reflections.Reflections;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserExportFileTypeFactory {
    /**
     * 标记是否未初始化数据，只初始化一次
     **/
    private static volatile boolean isUninitialized = true;

    private static final Set<IUserExportFileType> set = new HashSet<>();

    public static Set<IUserExportFileType> getProcessStepHandlerTypeSet() {
        if (isUninitialized) {
            synchronized (UserExportFileTypeFactory.class) {
                if (isUninitialized) {
                    Reflections reflections = new Reflections("neatlogic");
                    Set<Class<? extends IUserExportFileType>> classSet = reflections.getSubTypesOf(IUserExportFileType.class);
                    for (Class<? extends IUserExportFileType> c : classSet) {
                        Collections.addAll(set, c.getEnumConstants());
                    }
                    isUninitialized = false;
                }
            }
        }
        return set;
    }

    public static String getText(String value) {
        for (IUserExportFileType s : getProcessStepHandlerTypeSet()) {
            if (s.getValue().equals(value)) {
                return s.getText();
            }
        }
        return "";
    }

    public static IUserExportFileType getUserExportFileType(String value) {
        for (IUserExportFileType s : getProcessStepHandlerTypeSet()) {
            if (s.getValue().equals(value)) {
                return s;
            }
        }
        return null;
    }
}
