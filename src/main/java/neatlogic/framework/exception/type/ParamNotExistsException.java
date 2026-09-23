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
import neatlogic.framework.util.$;

import java.util.ArrayList;
import java.util.List;

public class ParamNotExistsException extends ApiRuntimeException {

    private static final long serialVersionUID = -2608671566655133328L;

    /**
     * 构造单个必填参数缺失异常。
     */
    public ParamNotExistsException(String paramNames) {
        super("nfe.paramnotexistsexception.required", paramNames);
    }

    /**
     * 构造数组元素字段缺失异常。
     */
    public ParamNotExistsException(int index, String keyName) {
        super("nfe.paramnotexistsexception.indexedfieldrequired", index, keyName);
    }

    /**
     * 构造具名数组参数元素字段缺失异常。
     */
    public ParamNotExistsException(int index, String paramName, String keyName) {
        super("nfe.paramnotexistsexception.namedindexedfieldrequired", index, paramName, keyName);
    }

    /**
     * 构造多个参数不能同时为空的异常。
     */
    public ParamNotExistsException(String... paramNames) {
        super("nfe.paramnotexistsexception.allblank", String.join($.t("nfe.paramnotexistsexception.groupseparator"), paramNames));
    }

    /**
     * 构造互斥参数组至少填写一组的异常。
     */
    @SafeVarargs
    public ParamNotExistsException(List<String>... paramGroups) {
        super("nfe.paramnotexistsexception.selectonegroup", buildGroupText(paramGroups));
    }

    /**
     * 根据当前语言连接参数组，仅格式化参数标识，不翻译业务数据。
     */
    private static String buildGroupText(List<String>... paramGroups) {
        List<String> groupStrList = new ArrayList<>();
        for (List<String> group : paramGroups) {
            groupStrList.add("[" + String.join($.t("nfe.paramnotexistsexception.groupseparator"), group) + "]");
        }
        return String.join($.t("nfe.paramnotexistsexception.groupseparator"), groupStrList);
    }
}
