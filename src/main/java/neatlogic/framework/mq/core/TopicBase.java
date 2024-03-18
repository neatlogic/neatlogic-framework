/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
                if (topicVo != null && Objects.equals(topicVo.getIsActive(), 1)) {
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
