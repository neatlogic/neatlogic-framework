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
            /* ?????????????????????????????????????????????????????????????????? **/
            if (currentTimeMillis >= messageCache.getMaxDelayTime()) {
                break;
            }
            /* ?????????????????????????????????????????????????????????????????? **/
            if (currentTimeMillis >= messageCache.getMinDelayTime()) {
                break;
            }
            /* ?????????????????????????????? **/
            try {
                TimeUnit.MILLISECONDS.sleep(messageCache.getMinDelayTime() - currentTimeMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            MessageCache.clear();
            /* ?????????????????????????????? **/
            messageCache.setExpired(true);
            while (messageCache.getWritingDataThreadNum() > 0) {
                /* ????????????????????????????????????????????????????????? **/
                synchronized (messageCache.getLOCK()) {
                    /* ?????????????????????????????????????????????????????????????????????????????????????????? **/
                    messageCache.getLOCK().wait(50);
                }
            }
            ConcurrentMap<NotifyVo, Object> notifyVoMap = messageCache.getNotifyVoMap();
            /* ?????????????????? **/
//            System.out.println(TenantContext.get().getTenantUuid() + ":" + notifyVoMap.size());
//            Test.putAllNotifyVoMap(notifyVoMap);
            sendMessage(notifyVoMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * ????????????
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
                    /* ????????????????????????????????????????????????????????????????????????????????? **/
                    if (messageHandler.getNeedCompression()) {
                        notifyList.add(messageHandler.compress(notifyVoList));
                    } else {
                        notifyList = notifyVoList;
                    }
                } else {
                    notifyList = notifyVoList;
                }
                /* ???????????????????????????????????? **/
                long expireTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(Config.USER_EXPIRETIME());
                /* ?????????????????????????????????????????????????????????????????????????????? **/
                List<String> onlineUserUuidList = userSessionMapper.getOnlineUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidListAndGreaterThanSessionTime(
                        messageHandlerAndRecipientVo.getToUserUuidList(), messageHandlerAndRecipientVo.getToTeamUuidList(), messageHandlerAndRecipientVo.getToRoleUuidList(), new Date(expireTime), false, null, null);

                List<String> toUserUuidList = messageHandlerAndRecipientVo.getToUserUuidList();
                if (CollectionUtils.isNotEmpty(toUserUuidList)) {
                    if (CollectionUtils.isNotEmpty(onlineUserUuidList)) {
                        /* ???????????????????????? **/
                        toUserUuidList.removeAll(onlineUserUuidList);
                    }
                    if (CollectionUtils.isNotEmpty(toUserUuidList)) {
                        List<String> unsubscribedUserUuidList = messageMapper.getMessageUnsubscribedUserUuidListByHandlerAndUserUuidList(handler, toUserUuidList);
                        if (CollectionUtils.isNotEmpty(unsubscribedUserUuidList)) {
                            /* ?????????????????????????????? **/
                            toUserUuidList.removeAll(unsubscribedUserUuidList);
                        }
                    }
                }

                /* ??????????????????????????????????????? **/
                if (CollectionUtils.isNotEmpty(onlineUserUuidList)) {
                    List<String> unsubscribedUserUuidList = messageMapper.getMessageUnsubscribedUserUuidListByHandlerAndUserUuidList(handler, onlineUserUuidList);
                    if (CollectionUtils.isNotEmpty(unsubscribedUserUuidList)) {
                        /* ?????????????????????????????? **/
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
     * ????????????????????????
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
