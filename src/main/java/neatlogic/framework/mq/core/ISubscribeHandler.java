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

package neatlogic.framework.mq.core;

import neatlogic.framework.mq.dto.SubscribeVo;

public interface ISubscribeHandler {
    String getName();

    String getLabel();
    //void onMessage(TextMessage m, Session session, String topicName, String subscribeName, String tenantUuid);

    //void onMessage(String message, String topicName, String subscribeName, String tenantUuid);

    void onMessage(SubscribeVo subscribeVo, Object message);

    default String getClassName() {
        return this.getClass().getName();
    }
}
