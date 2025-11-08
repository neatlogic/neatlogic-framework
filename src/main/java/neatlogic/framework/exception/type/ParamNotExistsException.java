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

package neatlogic.framework.exception.type;

import neatlogic.framework.exception.core.ApiRuntimeException;

import java.util.ArrayList;
import java.util.List;

public class ParamNotExistsException extends ApiRuntimeException {

    private static final long serialVersionUID = -2608671566655133328L;

    public ParamNotExistsException(String paramNames) {
        super("参数“{0}”不能为空", paramNames);
    }

    public ParamNotExistsException(int index, String keyName) {
        super("第{0}个参数的“{2}”不能为空", index, keyName);
    }

    public ParamNotExistsException(int index, String paramName, String keyName) {
        super("第{0}个参数“{1}”的“{2}”不能为空", index, paramName, keyName);
    }

    public ParamNotExistsException(String... paramNames) {
        super("参数“{0}”不能同时为空", String.join("、", paramNames));
    }

    @SafeVarargs
    public ParamNotExistsException(List<String>... paramGroups) {
        super(buildMessage(paramGroups));
    }

    private static String buildMessage(List<String>... paramGroups) {
        List<String> groupStrList = new ArrayList<>();
        for (List<String> group : paramGroups) {
            groupStrList.add("[" + String.join("、", group) + "]");
        }
        return "必须在" + String.join("、", groupStrList) + "中选择一组填写";
    }
}
