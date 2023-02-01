/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.mq.dao.mapper;

import neatlogic.framework.mq.dto.SubscribeVo;

import java.util.List;

public interface MqSubscribeMapper {
    List<SubscribeVo> searchSubscribe(SubscribeVo subscriberVo);

    int checkSubscribeNameIsExists(SubscribeVo subscribeVo);

    SubscribeVo getSubscribeById(Long id);

    SubscribeVo getSubscribeByName(String name);

    int searchSubscribeCount(SubscribeVo subscriberVo);

    void insertSubscribe(SubscribeVo subscribeVo);

    void updateSubscribe(SubscribeVo subscribeVo);

    void updateSubscribeServerId(SubscribeVo subscribeVo);

    void updateSubscribeError(SubscribeVo subscribeVo);

    void deleteSubscribeById(Long id);
}
