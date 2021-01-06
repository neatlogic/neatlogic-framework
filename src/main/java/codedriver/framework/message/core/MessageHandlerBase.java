package codedriver.framework.message.core;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserSessionVo;
import codedriver.framework.message.dao.mapper.MessageMapper;
import codedriver.framework.message.dto.MessageRecipientVo;
import codedriver.framework.message.dto.MessageSearchVo;
import codedriver.framework.message.dto.MessageVo;
import codedriver.framework.notify.dto.NotifyReceiverVo;
import codedriver.framework.notify.dto.NotifyVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Title: MessageHandlerBase
 * @Package codedriver.framework.message.core
 * @Description: 消息处理器基类
 * @Author: linbq
 * @Date: 2020/12/30 17:25
 **/
public abstract class MessageHandlerBase implements IMessageHandler {

    private static MessageMapper messageMapper;
    private static UserMapper userMapper;
    private static TeamMapper teamMapper;

    @Autowired
    public void setMessageMapper(MessageMapper _messageMapper){
        messageMapper = _messageMapper;
    }
    @Autowired
    public void setUserMapper(UserMapper _userMapper){
        userMapper = _userMapper;
    }
    @Autowired
    public void setTeamMapper(TeamMapper _teamMapper){
        teamMapper = _teamMapper;
    }

    @Override
    public void send(NotifyVo notifyVo) {
        MessageVo messageVo = new MessageVo();
        messageVo.setTitle(notifyVo.getTitle());
        messageVo.setContent(notifyVo.getContent());
        messageVo.setHandler(notifyVo.getMessageHandlerClass().getName());
        List<MessageVo> messageVoList = new ArrayList<>();
        messageVoList.add(messageVo);
        messageMapper.insertMessage(messageVoList);
        List<MessageRecipientVo> messageRecipientVoList = new ArrayList<>();
        long expireTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(Config.USER_EXPIRETIME());
        List<String> onlineUserUuidList = userMapper.getOnlineUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidListAndGreaterThanSessionTime(notifyVo.getToUserUuidList(), notifyVo.getToTeamUuidList(), notifyVo.getToRoleUuidList(), new Date(expireTime));
        if(CollectionUtils.isNotEmpty(notifyVo.getToUserUuidList())){
            List<String> toUserUuidList = ListUtils.removeAll(notifyVo.getToUserUuidList(), onlineUserUuidList);
            for(String userUuid : toUserUuidList){
                messageRecipientVoList.add(new MessageRecipientVo(messageVo.getId(), GroupSearch.USER.getValue(), userUuid));
            }
        }
        for(String teamUuid : notifyVo.getToTeamUuidList()){
            messageRecipientVoList.add(new MessageRecipientVo(messageVo.getId(), GroupSearch.USER.getValue(), teamUuid));
        }
        for(String roleUuid : notifyVo.getToRoleUuidList()){
            messageRecipientVoList.add(new MessageRecipientVo(messageVo.getId(), GroupSearch.USER.getValue(), roleUuid));
        }
        messageMapper.insertMessageRecipient(messageRecipientVoList);
        /** 直接发送给已订阅的在线用户 **/
        if(CollectionUtils.isNotEmpty(onlineUserUuidList)){
            List<MessageSearchVo> messageSearchVoList = new ArrayList<>();
            for(String userUuid : onlineUserUuidList){
                messageSearchVoList.add(new MessageSearchVo(userUuid, messageVo.getId()));
            }
            messageMapper.insertMessageUser(messageSearchVoList);
        }
    }
}
