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

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.mq.dao.mapper.MqTopicMapper;
import neatlogic.framework.mq.dto.TopicVo;
import neatlogic.framework.transaction.core.AfterTransactionJob;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.Objects;

public abstract class TopicBase<T> implements ITopic<T> {
    private static final Logger logger = LoggerFactory.getLogger(TopicBase.class);
    //protected static JmsTemplate jmsTemplate;

   /* @Autowired
    public void setJmsTemplate(JmsTemplate _jmsTemplate) {
        jmsTemplate = _jmsTemplate;
    }*/

    protected static MqTopicMapper mqTopicMapper;

    @Autowired
    public void setMqMapper(MqTopicMapper _mqTopicMapper) {
        mqTopicMapper = _mqTopicMapper;
    }

    @Override
    public final void send(T content) {
        if (content != null) {
            AfterTransactionJob<T> job = new AfterTransactionJob<>("JMS-SENDER");
            job.execute(content, t -> {
                String topicName = this.getName().toLowerCase(Locale.ROOT);
                TopicVo topicVo = mqTopicMapper.getTopicByName(topicName);
                //如果数据库没有主题设置数据，尝试在内存中加载
                if (topicVo == null) {
                    topicVo = TopicFactory.getTopicByName(topicName);
                }
                if (topicVo != null && StringUtils.isNotBlank(topicVo.getHandler()) && Objects.equals(topicVo.getIsActive(), 1)) {
                    IMqHandler handler = MqHandlerFactory.getMqHandler(topicVo.getHandler());
                    if (handler != null && handler.isEnable()) {
                        JSONObject contentObj = generateTopicContent(topicVo, content);
                        if (MapUtils.isNotEmpty(contentObj)) {
                            String msg = contentObj.toString();
                            try {
                                handler.send(topicName, msg);
                                //jmsTemplate.convertAndSend(TenantContext.get().getTenantUuid() + "/" + topicName, msg);
                            } catch (Exception ex) {
                                logger.error("发送消息到{}/{}失败，异常：{}", TenantContext.get().getTenantUuid(), topicName, ex.getMessage());
                            }
                            logger.info("send msg to topic[{}/{}]{}", TenantContext.get().getTenantUuid(), topicName, msg);
                        }
                    }
                }
            });
        }
    }

    protected abstract JSONObject generateTopicContent(TopicVo topicVo, T content);
}
