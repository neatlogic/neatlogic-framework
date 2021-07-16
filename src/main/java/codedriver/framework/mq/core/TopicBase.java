/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.mq.core;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.mq.dao.mapper.MqTopicMapper;
import codedriver.framework.mq.dto.TopicVo;
import codedriver.framework.transaction.core.AfterTransactionJob;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import java.util.Locale;

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
            AfterTransactionJob<T> job = new AfterTransactionJob<>();
            job.execute(content, t -> {
                String topicName = this.getName().toLowerCase(Locale.ROOT);
                TopicVo topicVo = mqTopicMapper.getTopicByName(topicName);
                if (topicVo == null || topicVo.getIsActive().equals(1)) {
                    JSONObject contentObj = generateTopicContent(content);
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

    protected abstract JSONObject generateTopicContent(T content);
}
