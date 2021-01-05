package codedriver.framework.news.dao.mapper;

import codedriver.framework.news.dto.*;

import java.util.Date;
import java.util.List;

/**
 * @Title: NewsMapper
 * @Package codedriver.framework.news.dao.mapper
 * @Description: 消息通知Mapper
 * @Author: linbq
 * @Date: 2020/12/30 16:00
 **/
public interface NewsMapper {

    public List<NewsHandlerVo> getNewsSubscribeListByUserUuid(String userUuid);

    public NewsHandlerVo getNewsSubscribeByUserUuidAndHandler(NewsHandlerVo newsHandlerVo);

    public int getNewsMessageCount(NewsMessageSearchVo searchVo);

    public List<NewsMessageVo> getNewsMessageList(NewsMessageSearchVo searchVo);

    public int getNewsMessageNewCount(NewsMessageSearchVo searchVo);

    public int getNewsMessageHistoryCount(NewsMessageSearchVo searchVo);

    public List<NewsMessageVo> getNewsMessageHistoryList(NewsMessageSearchVo searchVo);

    public int getNewsMessagePullCount(NewsMessageSearchVo searchVo);

    public List<Long> getNewsMessagePullList(NewsMessageSearchVo searchVo);

    public List<NewsMessageVo> getNewsMessageListByIdList(List<Long> newsMessageIdList);

    public Long getNewsMessageUserMaxNewsMessageIdByUserUuid(String userUuid);

    public Date getNewsMessageFcdById(Long maxNewsMessageId);

    public int insertNewsMessage(List<NewsMessageVo> newsMessageVoList);

    public int insertNewsMessageRecipient(List<NewsMessageRecipientVo> newsMessageRecipientVoList);

    public int insertNewsMessageUser(List<NewsMessageSearchVo> newsMessageSearchVoList);

    public int insertNewsSubscribe(NewsHandlerVo newsHandlerVo);

    public int updateNewsMessageUserIsRead(NewsMessageSearchVo newsMessageSearchVo);

    public int updateNewsMessageUserIsDelete(NewsMessageSearchVo newsMessageSearchVo);

    public int updateNewsSubscribePopUp(NewsHandlerVo newsHandlerVo);

    public int updateNewsSubscribeActive(NewsHandlerVo newsHandlerVo);
}
