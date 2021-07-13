/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.mq.dao.mapper;

import codedriver.framework.mq.dto.SubscribeVo;

import java.util.List;

public interface MqSubscribeMapper {
    List<SubscribeVo> searchSubscribe(SubscribeVo subscriberVo);

    SubscribeVo getSubscribeByName(String name);

    int searchSubscribeCount(SubscribeVo subscriberVo);

    void updateSubscribeServerId(SubscribeVo subscribeVo);

    void updateSubscribeError(SubscribeVo subscribeVo);
}
