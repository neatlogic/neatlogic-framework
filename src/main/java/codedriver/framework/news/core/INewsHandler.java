package codedriver.framework.news.core;

import codedriver.framework.news.dto.NewsMessageVo;
import org.springframework.util.ClassUtils;

/**
 * @Title: INewsHandler
 * @Package codedriver.framework.news.core
 * @Description: 消息处理器接口
 * @Author: linbq
 * @Date: 2020/12/30 15:09
 **/
public interface INewsHandler {
    /**
     * @Description:
     * @Author: linbq
     * @Date: 2020/12/30 15:10
     * @Params:[]
     * @Returns:java.lang.String
     **/
    String getName();
    /**
     * @Description:
     * @Author: linbq
     * @Date: 2020/12/30 15:11
     * @Params:[]
     * @Returns:java.lang.String
     **/
    default String getHandler(){
        return ClassUtils.getUserClass(this.getClass()).getName();
    }
    /**
     * @Description:
     * @Author: linbq
     * @Date: 2020/12/30 15:11
     * @Params:[]
     * @Returns:java.lang.String
     **/
    String getDescription();
    /**
     * @Description:
     * @Author: linbq
     * @Date: 2020/12/30 15:11
     * @Params:[messageVo]
     * @Returns:void
     **/
    void send(NewsMessageVo messageVo);
}
