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

package neatlogic.framework.util.jsondiff.common.function;

import java.util.HashSet;

@FunctionalInterface
public interface Function {

    /**
     * 根据当前的路径。判断是否需要进行指定key联系对象
     *
     * @param exPath 期望对象的路径
     * @param acPath 真实对象的路径
     * @return 返回一个key的集合
     */
    HashSet<String> apply(String exPath, String acPath);
}

