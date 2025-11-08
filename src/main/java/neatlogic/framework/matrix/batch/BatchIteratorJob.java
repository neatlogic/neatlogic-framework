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

package neatlogic.framework.matrix.batch;

import java.util.Iterator;
import java.util.List;
/**
 * 批量处理
 *
 * @author lvzk
 * @since 2024/6/3 10:31 上午
 **/
public interface BatchIteratorJob<T> {
    void execute(Iterator<T> item, List<T> itemList);
}
