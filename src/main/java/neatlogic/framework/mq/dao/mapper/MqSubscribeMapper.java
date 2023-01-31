/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
