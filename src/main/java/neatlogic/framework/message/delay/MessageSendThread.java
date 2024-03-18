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

package neatlogic.framework.message.delay;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.dao.mapper.UserMapper;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.message.core.IMessageHandler;
import neatlogic.framework.message.core.MessageHandlerFactory;
import neatlogic.framework.message.dao.mapper.MessageMapper;
import neatlogic.framework.message.dto.MessageRecipientVo;
import neatlogic.framework.message.dto.MessageSearchVo;
import neatlogic.framework.message.dto.MessageVo;
import neatlogic.framework.notify.dto.NotifyVo;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Component
public class MessageSendThread extends NeatLogicThread {

    private static final Logger logger = LoggerFactory.getLogger(MessageSendThread.class);
    private final static int BATCH_INSERT_MAX_COUNT = 1000;
    private static MessageMapper messageMapper;
    private static UserMapper userMapper;
    private static UserSessionMapper userSessionMapper;

    @Autowired
    public void setMessageMapper(MessageMapper _messageMapper) {
        messageMapper = _messageMapper;
    }

    @Autowired
    public void setUserMapper(UserMapper _userMapper) {
        userMapper = _userMapper;
    }

    @Autowired
    public void setUserSessionMapper(UserSessionMapper _userSessionMapper) {
        userSessionMapper = _userSessionMapper;
    }
    private MessageCache messageCache;

    public MessageSendThread() {
        super("MESSAGE-SENDER-" + TenantContext.get().getTenantUuid());
    }

    public MessageSendThread(MessageCache messageCache) {
        super("MESSAGE-SENDER-" + TenantContext.get().getTenantUuid());
        this.messageCache = messageCache;
    }

    @Override
    protected void execute() {
        while (true) {
            long currentTimeMillis = System.currentTimeMillis();
            /* 如果当前时间大于最大延迟时间，则开始发送信息 **/
            if (currentTimeMillis >= messageCache.getMaxDelayTime()) {
                break;
            }
            /* 如果当前时间大于最小延迟时间，则开始发送信息 **/
            if (currentTimeMillis >= messageCache.getMinDelayTime()) {
                break;
            }
            /* 睡眠到最小的延迟时间 **/
            try {
                TimeUnit.MILLISECONDS.sleep(messageCache.getMinDelayTime() - currentTimeMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            MessageCache.clear();
            /* 将缓存对象设置为失效 **/
            messageCache.setExpired(true);
            while (messageCache.getWritingDataThreadNum() > 0) {
                /* 如果还有线程正在往当前缓存对象中写数据 **/
                synchronized (messageCache.getLOCK()) {
                    /* 等待所有正在往当前缓存对象中写数据的线程完成后，唤醒当前线程 **/
                    messageCache.getLOCK().wait(50);
                }
            }
            ConcurrentMap<NotifyVo, Object> notifyVoMap = messageCache.getNotifyVoMap();
            /* 开始处理消息 **/
//            System.out.println(TenantContext.get().getTenantUuid() + ":" + notifyVoMap.size());
//            Test.putAllNotifyVoMap(notifyVoMap);
            sendMessage(notifyVoMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 发送消息
     *
     * @param notifyVoMap
     */
    private void sendMessage(ConcurrentMap<NotifyVo, Object> notifyVoMap) {
        Map<NotifyVo.MessageHandlerAndRecipientVo, List<NotifyVo>> notifyVoListMap = new HashMap<>();
        for (NotifyVo notifyVo : notifyVoMap.keySet()) {
            notifyVoListMap.computeIfAbsent(notifyVo.getMessageHandlerAndRecipientVo(), k -> new ArrayList<>()).add(notifyVo);
        }
        List<MessageVo> messageVoList = new ArrayList<>(BATCH_INSERT_MAX_COUNT);
        List<MessageRecipientVo> messageRecipientVoList = new ArrayList<>();
        List<MessageSearchVo> messageSearchVoList = new ArrayList<>();
        for (Map.Entry<NotifyVo.MessageHandlerAndRecipientVo, List<NotifyVo>> entry : notifyVoListMap.entrySet()) {
            NotifyVo.MessageHandlerAndRecipientVo messageHandlerAndRecipientVo = entry.getKey();
            String handler = messageHandlerAndRecipientVo.getMessageHandlerClass().getName();
            IMessageHandler messageHandler = MessageHandlerFactory.getHandler(handler);
            if (messageHandler != null) {
                List<NotifyVo> notifyList = new ArrayList<>();
                List<NotifyVo> notifyVoList = entry.getValue();
                if (notifyVoList.size() > 1) {
                    /* 如果该消息类型同时有多条消息，且需要压缩，才压缩成一条 **/
                    if (messageHandler.getNeedCompression()) {
                        notifyList.add(messageHandler.compress(notifyVoList));
                    } else {
                        notifyList = notifyVoList;
                    }
                } else {
                    notifyList = notifyVoList;
                }
                /* 计算出用户登录失效时间点 **/
                long expireTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(Config.USER_EXPIRETIME());
                /* 根据用户、组、角色和登录失效时间点，找出所有在线用户 **/
                List<String> onlineUserUuidList = userSessionMapper.getOnlineUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidListAndGreaterThanSessionTime(
                        messageHandlerAndRecipientVo.getToUserUuidList(), messageHandlerAndRecipientVo.getToTeamUuidList(), messageHandlerAndRecipientVo.getToRoleUuidList(), new Date(expireTime), false, null, null);

                List<String> toUserUuidList = messageHandlerAndRecipientVo.getToUserUuidList();
                if (CollectionUtils.isNotEmpty(toUserUuidList)) {
                    if (CollectionUtils.isNotEmpty(onlineUserUuidList)) {
                        /* 去掉所有在线用户 **/
                        toUserUuidList.removeAll(onlineUserUuidList);
                    }
                    if (CollectionUtils.isNotEmpty(toUserUuidList)) {
                        List<String> unsubscribedUserUuidList = messageMapper.getMessageUnsubscribedUserUuidListByHandlerAndUserUuidList(handler, toUserUuidList);
                        if (CollectionUtils.isNotEmpty(unsubscribedUserUuidList)) {
                            /* 去掉所有取消订阅用户 **/
                            toUserUuidList.removeAll(unsubscribedUserUuidList);
                        }
                    }
                }

                /* 直接发送给已订阅的在线用户 **/
                if (CollectionUtils.isNotEmpty(onlineUserUuidList)) {
                    List<String> unsubscribedUserUuidList = messageMapper.getMessageUnsubscribedUserUuidListByHandlerAndUserUuidList(handler, onlineUserUuidList);
                    if (CollectionUtils.isNotEmpty(unsubscribedUserUuidList)) {
                        /* 去掉所有取消订阅用户 **/
                        onlineUserUuidList.removeAll(unsubscribedUserUuidList);
                    }
                }
                for (NotifyVo notifyVo : notifyList) {
                    MessageVo messageVo = new MessageVo(notifyVo);
                    messageVoList.add(messageVo);
                    for (String userUuid : toUserUuidList) {
                        messageRecipientVoList.add(new MessageRecipientVo(messageVo.getId(), GroupSearch.USER.getValue(), userUuid));
                    }
                    for (String teamUuid : notifyVo.getToTeamUuidList()) {
                        messageRecipientVoList.add(new MessageRecipientVo(messageVo.getId(), GroupSearch.TEAM.getValue(), teamUuid));
                    }
                    for (String roleUuid : notifyVo.getToRoleUuidList()) {
                        messageRecipientVoList.add(new MessageRecipientVo(messageVo.getId(), GroupSearch.ROLE.getValue(), roleUuid));
                    }
                    for (String userUuid : onlineUserUuidList) {
                        messageSearchVoList.add(new MessageSearchVo(userUuid, messageVo.getId()));
                    }
                    if (messageVoList.size() >= BATCH_INSERT_MAX_COUNT) {
                        batchInsertData(messageVoList, messageRecipientVoList, messageSearchVoList);
                    }
                }
            }
        }
        batchInsertData(messageVoList, messageRecipientVoList, messageSearchVoList);
    }

    /**
     * 批量插入消息数据
     *
     * @param messageVoList
     * @param messageRecipientVoList
     * @param messageSearchVoList
     */
    private void batchInsertData(List<MessageVo> messageVoList, List<MessageRecipientVo> messageRecipientVoList, List<MessageSearchVo> messageSearchVoList) {
        if (CollectionUtils.isNotEmpty(messageVoList)) {
            messageMapper.insertMessage(messageVoList);
            messageVoList.clear();
        }

        if (CollectionUtils.isNotEmpty(messageRecipientVoList)) {
            int count = messageRecipientVoList.size() / BATCH_INSERT_MAX_COUNT + 1;
            for (int i = 0; i < count; i++) {
                int fromIndex = i * BATCH_INSERT_MAX_COUNT;
                int toIndex = fromIndex + BATCH_INSERT_MAX_COUNT;
                toIndex = Math.min(toIndex, messageRecipientVoList.size());
                messageMapper.insertMessageRecipient(messageRecipientVoList.subList(fromIndex, toIndex));
            }
            messageRecipientVoList.clear();
        }
        if (CollectionUtils.isNotEmpty(messageSearchVoList)) {
            int count = messageSearchVoList.size() / BATCH_INSERT_MAX_COUNT + 1;
            for (int i = 0; i < count; i++) {
                int fromIndex = i * BATCH_INSERT_MAX_COUNT;
                int toIndex = fromIndex + BATCH_INSERT_MAX_COUNT;
                toIndex = Math.min(toIndex, messageSearchVoList.size());
                messageMapper.insertMessageUser(messageSearchVoList.subList(fromIndex, toIndex));
            }
            messageSearchVoList.clear();
        }
    }
}
