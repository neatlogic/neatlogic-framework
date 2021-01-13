package codedriver.framework.message.delay;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.message.core.IMessageHandler;
import codedriver.framework.message.core.MessageHandlerFactory;
import codedriver.framework.message.dao.mapper.MessageMapper;
import codedriver.framework.message.dto.MessageRecipientVo;
import codedriver.framework.message.dto.MessageSearchVo;
import codedriver.framework.message.dto.MessageVo;
import codedriver.framework.notify.dto.NotifyVo;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @Title: MessageSendThread
 * @Package codedriver.framework.message.delay
 * @Description: 监听缓存对象的任务
 * @Author: linbq
 * @Date: 2021/1/10 16:45
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@Component
public class MessageSendThread extends CodeDriverThread {

    private static Logger logger = LoggerFactory.getLogger(MessageSendThread.class);
    private final static int BATCH_INSERT_MAX_COUNT = 1000;
    private static MessageMapper messageMapper;
    private static UserMapper userMapper;

    @Autowired
    public void setMessageMapper(MessageMapper _messageMapper){
        messageMapper = _messageMapper;
    }
    @Autowired
    public void setUserMapper(UserMapper _userMapper){
        userMapper = _userMapper;
    }

    private MessageCache messageCache;

    public MessageSendThread(){

    }
    public MessageSendThread(MessageCache messageCache) {
        super.setThreadName("MESSAGE-SEND-THREAD-" + TenantContext.get().getTenantUuid());
        this.messageCache = messageCache;
    }

    @Override
    protected void execute() {
        while (true) {
            long currentTimeMillis = System.currentTimeMillis();
            /** 如果当前时间大于最大延迟时间，则开始发送信息 **/
            if (currentTimeMillis >= messageCache.getMaxDelayTime()) {
                break;
            }
            /** 如果当前时间大于最小延迟时间，则开始发送信息 **/
            if (currentTimeMillis >= messageCache.getMinDelayTime()) {
                break;
            }
            /** 睡眠到最小的延迟时间 **/
            try {
                TimeUnit.MILLISECONDS.sleep(messageCache.getMinDelayTime() - currentTimeMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            MessageCache.clear();
            /** 将缓存对象设置为失效 **/
            messageCache.setExpired(true);
            while (messageCache.getWritingDataThreadNum() > 0) {
                /** 如果还有线程正在往当前缓存对象中写数据 **/
                synchronized (messageCache.getLOCK()) {
                    /** 等待所有正在往当前缓存对象中写数据的线程完成后，唤醒当前线程 **/
                    messageCache.getLOCK().wait();
                }
            }
            ConcurrentMap<NotifyVo, Object> notifyVoMap = messageCache.getNotifyVoMap();
            /** 开始处理消息 **/
//            System.out.println(TenantContext.get().getTenantUuid() + ":" + notifyVoMap.size());
//            Test.putAllNotifyVoMap(notifyVoMap);
            sendMessage(notifyVoMap);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    /**
     * @Description: 发送消息
     * @Author: linbq
     * @Date: 2021/1/13 11:30
     * @Params:[notifyVoMap]
     * @Returns:void
     **/
    private void sendMessage(ConcurrentMap<NotifyVo, Object> notifyVoMap){
        Map<NotifyVo.MessageHandlerAndRecipientVo, List<NotifyVo>> notifyVoListMap = new HashMap<>();
        for(NotifyVo notifyVo : notifyVoMap.keySet()){
            notifyVoListMap.computeIfAbsent(notifyVo.getMessageHandlerAndRecipientVo(), k -> new ArrayList<>()).add(notifyVo);
        }
        List<MessageVo> messageVoList = new ArrayList<>(BATCH_INSERT_MAX_COUNT);
        List<MessageRecipientVo> messageRecipientVoList = new ArrayList<>();
        List<MessageSearchVo> messageSearchVoList = new ArrayList<>();
        for(Map.Entry<NotifyVo.MessageHandlerAndRecipientVo, List<NotifyVo>> entry : notifyVoListMap.entrySet()){
            NotifyVo.MessageHandlerAndRecipientVo messageHandlerAndRecipientVo = entry.getKey();
            IMessageHandler messageHandler = MessageHandlerFactory.getHandler(messageHandlerAndRecipientVo.getMessageHandlerClass().getName());
            if(messageHandler != null){
                List<NotifyVo> notifyList = new ArrayList<>();
                List<NotifyVo> notifyVoList = entry.getValue();
                if(notifyVoList.size() > 1){
                    /** 如果该消息类型同时有多条消息，且需要压缩，才压缩成一条 **/
                    if(messageHandler.getNeedCompression()){
                        notifyList.add(messageHandler.compress(notifyVoList));
                    }else {
                        notifyList = notifyVoList;
                    }
                }else{
                    notifyList = notifyVoList;
                }
                for(NotifyVo notifyVo : notifyList){
                    MessageVo messageVo = new MessageVo(notifyVo);
                    messageVoList.add(messageVo);
                    /** 计算出用户登录失效时间点 **/
                    long expireTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(Config.USER_EXPIRETIME());
                    /** 根据用户、组、角色和登录失效时间点，找出所有在线用户 **/
                    List<String> onlineUserUuidList = userMapper.getOnlineUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidListAndGreaterThanSessionTime(
                            notifyVo.getToUserUuidList(), notifyVo.getToTeamUuidList(), notifyVo.getToRoleUuidList(), new Date(expireTime));
                    if(CollectionUtils.isNotEmpty(notifyVo.getToUserUuidList())){
                        List<String> toUserUuidList = notifyVo.getToUserUuidList();
                        if(CollectionUtils.isNotEmpty(onlineUserUuidList)){
                            /** 去掉所有在线用户 **/
                            toUserUuidList.removeAll(onlineUserUuidList);
                        }
                        if(CollectionUtils.isNotEmpty(toUserUuidList)) {
                            List<String> unsubscribedUserUuidList = messageMapper.getMessageUnsubscribedUserUuidListByHandlerAndUserUuidList(messageVo.getHandler(), toUserUuidList);
                            if(CollectionUtils.isNotEmpty(unsubscribedUserUuidList)){
                                /** 去掉所有取消订阅用户 **/
                                toUserUuidList.removeAll(unsubscribedUserUuidList);
                            }
                            for (String userUuid : toUserUuidList) {
                                messageRecipientVoList.add(new MessageRecipientVo(messageVo.getId(), GroupSearch.USER.getValue(), userUuid));
                            }
                        }
                    }
                    for(String teamUuid : notifyVo.getToTeamUuidList()){
                        messageRecipientVoList.add(new MessageRecipientVo(messageVo.getId(), GroupSearch.TEAM.getValue(), teamUuid));
                    }
                    for(String roleUuid : notifyVo.getToRoleUuidList()){
                        messageRecipientVoList.add(new MessageRecipientVo(messageVo.getId(), GroupSearch.ROLE.getValue(), roleUuid));
                    }

                    /** 直接发送给已订阅的在线用户 **/
                    if(CollectionUtils.isNotEmpty(onlineUserUuidList)){
                        List<String> unsubscribedUserUuidList = messageMapper.getMessageUnsubscribedUserUuidListByHandlerAndUserUuidList(messageVo.getHandler(), onlineUserUuidList);
                        if(CollectionUtils.isNotEmpty(unsubscribedUserUuidList)){
                            /** 去掉所有取消订阅用户 **/
                            onlineUserUuidList.removeAll(unsubscribedUserUuidList);
                        }
                        for(String userUuid : onlineUserUuidList){
                            messageSearchVoList.add(new MessageSearchVo(userUuid, messageVo.getId()));
                        }
                    }
                }
                if(messageVoList.size() >= BATCH_INSERT_MAX_COUNT){
                    batchInsertData(messageVoList, messageRecipientVoList, messageSearchVoList);
                }
            }
        }
        batchInsertData(messageVoList, messageRecipientVoList, messageSearchVoList);
    }

    /**
     * @Description: 批量插入消息数据
     * @Author: linbq
     * @Date: 2021/1/13 11:30
     * @Params:[messageVoList, messageRecipientVoList, messageSearchVoList]
     * @Returns:void
     **/
    private void batchInsertData(List<MessageVo> messageVoList, List<MessageRecipientVo> messageRecipientVoList, List<MessageSearchVo> messageSearchVoList){
        messageMapper.insertMessage(messageVoList);
        messageVoList.clear();
        if(CollectionUtils.isNotEmpty(messageRecipientVoList)){
            int count = messageRecipientVoList.size() / BATCH_INSERT_MAX_COUNT + 1;
            for(int i = 0; i < count; i++){
                int fromIndex = i * BATCH_INSERT_MAX_COUNT;
                int toIndex = fromIndex + BATCH_INSERT_MAX_COUNT;
                toIndex = toIndex < messageRecipientVoList.size() ? toIndex : messageRecipientVoList.size();
                messageMapper.insertMessageRecipient(messageRecipientVoList.subList(fromIndex, toIndex));
            }
            messageRecipientVoList.clear();
        }
        if(CollectionUtils.isNotEmpty(messageSearchVoList)){
            int count = messageSearchVoList.size() / BATCH_INSERT_MAX_COUNT + 1;
            for(int i = 0; i < count; i++){
                int fromIndex = i * BATCH_INSERT_MAX_COUNT;
                int toIndex = fromIndex + BATCH_INSERT_MAX_COUNT;
                toIndex = toIndex < messageSearchVoList.size() ? toIndex : messageSearchVoList.size();
                messageMapper.insertMessageUser(messageSearchVoList.subList(fromIndex, toIndex));
            }
            messageSearchVoList.clear();
        }
    }
}
