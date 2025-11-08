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

package neatlogic.framework.mq.dao.mapper;

import neatlogic.framework.mq.dto.SubscribeVo;

import java.util.List;

public interface MqSubscribeMapper {
    List<SubscribeVo> searchSubscribe(SubscribeVo subscriberVo);

    int checkSubscribeNameIsExists(SubscribeVo subscribeVo);

    int selectSubscribeCountByTopicName(String topicName);

    SubscribeVo getSubscribeById(Long id);

    SubscribeVo getSubscribeByName(String name);

    int searchSubscribeCount(SubscribeVo subscriberVo);

    void insertSubscribe(SubscribeVo subscribeVo);

    void updateSubscribe(SubscribeVo subscribeVo);

    void updateSubscribeServerId(SubscribeVo subscribeVo);

    void updateSubscribeError(SubscribeVo subscribeVo);

    void deleteSubscribeById(Long id);
}
