package codedriver.framework.message.core;

import codedriver.framework.message.dao.mapper.MessageMapper;
import codedriver.framework.message.dto.MessageRecipientVo;
import codedriver.framework.message.dto.MessageVo;
import codedriver.framework.notify.dto.NotifyReceiverVo;
import codedriver.framework.notify.dto.NotifyVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: NewsHandlerBase
 * @Package codedriver.framework.news.core
 * @Description: 消息处理器基类
 * @Author: linbq
 * @Date: 2020/12/30 17:25
 **/
public abstract class MessageHandlerBase implements IMessageHandler {

    private static MessageMapper messageMapper;

    public void setNewsMapper(MessageMapper _messageMapper){
        messageMapper = _messageMapper;
    }

    @Override
    public void send(NotifyVo notifyVo) {
        MessageVo messageVo = new MessageVo();
        messageVo.setTitle(notifyVo.getTitle());
        messageVo.setContent(notifyVo.getContent());
        messageVo.setHandler(notifyVo.getNewsHandlerClass().getName());
        List<MessageVo> messageVoList = new ArrayList<>();
        messageVoList.add(messageVo);
        messageMapper.insertMessage(messageVoList);
        List<MessageRecipientVo> messageRecipientVoList = new ArrayList<>();
        for(NotifyReceiverVo notifyReceiverVo : notifyVo.getNotifyReceiverVoList()){
            messageRecipientVoList.add(new MessageRecipientVo(messageVo.getId(), notifyReceiverVo.getType(), notifyReceiverVo.getUuid()));
        }
        messageMapper.insertMessageRecipient(messageRecipientVoList);
    }
}
