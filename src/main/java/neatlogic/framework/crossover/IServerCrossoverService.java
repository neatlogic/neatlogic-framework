/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package neatlogic.framework.crossover;

import java.util.List;

public interface IServerCrossoverService extends ICrossoverService {

    /**
     * 使用全量同组服务器而不是仅 startup，避免停机服务器创建的作业在同组内不可见。
     * @return
     */
    List<Integer> getCurrentGroupServerIdList();
}
