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

import neatlogic.framework.exception.mq.SubscribeTopicException;
import neatlogic.framework.mq.dto.HealthcheckResultVo;
import neatlogic.framework.mq.dto.SubscribeVo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IMqHandler {
    String getName();

    String getLabel();

    boolean create(SubscribeVo subVo) throws SubscribeTopicException, ExecutionException, InterruptedException;

    void reconnect(SubscribeVo subscribeVo) throws SubscribeTopicException, ExecutionException, InterruptedException;

    boolean isRunning(SubscribeVo subscribeVo);

    void destroy(SubscribeVo subscribeVo);

    void send(String topicName, String content);

    boolean isEnable();

    List<HealthcheckResultVo> healthCheck(SubscribeVo subVo);
}
