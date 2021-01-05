package codedriver.framework.news.core;

import codedriver.framework.news.dao.mapper.NewsMapper;
import codedriver.framework.news.dto.NewsMessageVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Title: NewsHandlerBase
 * @Package codedriver.framework.news.core
 * @Description: 消息处理器基类
 * @Author: linbq
 * @Date: 2020/12/30 17:25
 **/
public abstract class NewsHandlerBase implements INewsHandler {

    private static NewsMapper newsMapper;

    public void setNewsMapper(NewsMapper _newsMapper){
        newsMapper = _newsMapper;
    }

    @Override
    public void send(NewsMessageVo messageVo) {
        List<NewsMessageVo> messageVoList = new ArrayList<>();
        messageVoList.add(messageVo);
        newsMapper.insertNewsMessage(messageVoList);
    }
}
