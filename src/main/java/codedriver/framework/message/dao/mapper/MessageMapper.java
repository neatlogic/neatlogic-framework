package codedriver.framework.message.dao.mapper;

import codedriver.framework.message.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Title: MessageMapper
 * @Package codedriver.framework.message.dao.mapper
 * @Description: 消息通知Mapper
 * @Author: linbq
 * @Date: 2020/12/30 16:00
 **/
public interface MessageMapper {

    public List<MessageHandlerVo> getMessageSubscribeListByUserUuid(String userUuid);

    public MessageHandlerVo getMessageSubscribeByUserUuidAndHandler(MessageHandlerVo messageHandlerVo);

    public int getMessageCount(MessageSearchVo searchVo);

    public List<MessageVo> getMessageList(MessageSearchVo searchVo);

    public int getMessageHistoryCount(MessageSearchVo searchVo);

    public List<MessageVo> getMessageHistoryList(MessageSearchVo searchVo);

    public int getMessagePullCount(MessageSearchVo searchVo);

    public List<Long> getMessagePullList(MessageSearchVo searchVo);

    public List<MessageVo> getMessageListByIdList(List<Long> messageIdList);

    public Long getMessageUserMaxMessageIdByUserUuid(String userUuid);

    public Date getMessageFcdById(Long maxMessageId);

    public Long getMessageMinIdByGreaterThanFcd(Date fcd);

    public int insertMessage(List<MessageVo> messageVoList);

    public int insertMessageRecipient(List<MessageRecipientVo> messageRecipientVoList);

    public int insertMessageUser(List<MessageSearchVo> messageSearchVoList);

    public int insertMessageSubscribe(MessageHandlerVo messageHandlerVo);

    public int updateMessageUserIsRead(@Param("userUuid") String userUuid, @Param("messageIdList") List<Long> messageIdList);

    public int updateMessageSubscribePopUp(MessageHandlerVo messageHandlerVo);

    public int updateMessageSubscribeActive(MessageHandlerVo messageHandlerVo);

    public int deleteMessageUser(MessageSearchVo messageSearchVo);
}
