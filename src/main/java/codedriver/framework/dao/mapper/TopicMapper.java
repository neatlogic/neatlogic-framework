package codedriver.framework.dao.mapper;

import codedriver.framework.dto.TopicVo;

/**
 * @author longrf
 * @date 2022/4/8 4:02 下午
 */
public interface TopicMapper {

    TopicVo getTopic();

    void insertTopic(TopicVo topicVo);

    void deleteTopic();

}
