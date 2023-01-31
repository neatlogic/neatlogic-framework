package neatlogic.framework.message.dao.mapper;

import neatlogic.framework.message.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Title: MessageMapper
 * @Package neatlogic.framework.message.dao.mapper
 * @Description: 消息通知Mapper
 * @Author: linbq
 * @Date: 2020/12/30 16:00
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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

    public Long getMessageUserMaxMessageIdByUserUuid(String userUuid);

    public Date getMessageInsertTimeById(Long maxMessageId);

    public Long getMessageMaxIdByLessThanInsertTime(Date fcd);

    public List<String> getMessageUnsubscribedUserUuidListByHandlerAndUserUuidList(@Param("handler") String handler, @Param("userUuidList") List<String> userUuidList);

    public List<TriggerMessageCountVo> getTriggerMessageCountListGroupByTriggerAndIsRead(MessageSearchVo searchVo);

    public int getMessageShortShowPopUpCountByUserUuidAndHandlerList(MessageSearchVo messageSearchVo);

    public int getMessageLongShowPopUpCountByUserUuidAndHandlerList(MessageSearchVo messageSearchVo);

    public int getMessageUnreadCountByUserUuid(String userUuid);

    public MessageVo getLastPopUpMessage(MessageSearchVo messageSearchVo);

    public MessageVo getMessageByIdAndUserUuid(MessageSearchVo searchVo);

    public int insertMessage(List<MessageVo> messageVoList);

    public int insertMessageRecipient(List<MessageRecipientVo> messageRecipientVoList);

    public int insertMessageUser(List<MessageSearchVo> messageSearchVoList);

    public int insertMessageSubscribe(MessageHandlerVo messageHandlerVo);

    public int updateMessageUserIsReadByUserUuidAndMessageId(MessageSearchVo searchVo);

    public int updateMessageUserIsReadByUserUuidAndMessageIdList(MessageSearchVo searchVo);

    public int updateMessageUserIsReadByUserUuidAndMessageIdRange(MessageSearchVo searchVo);

    public int updateMessageUserIsReadByUserUuidAndKeywordAndTriggerList(MessageSearchVo searchVo);

    public int updateMessageUserIsReadByUserUuid(String userUuid);

    public int updateMessageSubscribePopUp(MessageHandlerVo messageHandlerVo);

    public int updateMessageSubscribeActive(MessageHandlerVo messageHandlerVo);

    public int updateMessageUserExpiredIsShow1To2AndExpiredTimeIsNullByUserUuid(String userUuid);

    public int updateMessageUserIsShowAndExpiredTimeByUserUuidAndHandlerList(MessageSearchVo messageSearchVo);

    public int updateMessageUserIsShow1To2AndIsRead0To1ByUserUuidAndMessageId(MessageSearchVo searchVo);

    public int deleteMessageUser(MessageSearchVo messageSearchVo);

    public int deleteMessageRecipientByLessThanOrEqualMessageId(Long messageId);

    public int deleteMessageUserByLessThanOrEqualMessageId(Long messageId);

    public int deleteMessageByLessThanOrEqualId(Long messageId);
}
