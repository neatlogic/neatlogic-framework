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

package neatlogic.framework.mq.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.mq.dao.mapper.MqTopicMapper;
import neatlogic.framework.mq.dto.TopicVo;
import neatlogic.framework.transaction.core.AfterTransactionJob;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.Locale;
import java.util.Objects;

public abstract class TopicBase<T> implements ITopic<T> {
    private static final Logger logger = LoggerFactory.getLogger(TopicBase.class);
    protected static JmsTemplate jmsTemplate;

    @Autowired
    public void setJmsTemplate(JmsTemplate _jmsTemplate) {
        jmsTemplate = _jmsTemplate;
    }

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
                if (topicVo == null || Objects.equals(topicVo.getIsActive(), 1)) {
                    JSONObject contentObj = generateTopicContent(topicVo, content);
                    if (MapUtils.isNotEmpty(contentObj)) {
                        String msg = contentObj.toString();
                        try {
                            jmsTemplate.convertAndSend(TenantContext.get().getTenantUuid() + "/" + topicName, msg);
                        } catch (Exception ex) {
                            logger.error("发送消息到" + TenantContext.get().getTenantUuid() + "/" + topicName + "失败，异常：" + ex.getMessage());
                        }
                        logger.info("send msg to topic[" + TenantContext.get().getTenantUuid() + "/" + topicName + "]" + msg);
                    }
                }
            });
        }
    }

    protected abstract JSONObject generateTopicContent(TopicVo topicVo, T content);
}
