package codedriver.framework.message.core;

import codedriver.framework.notify.dto.NotifyVo;
import org.springframework.util.ClassUtils;

/**
 * @Title: IMessageHandler
 * @Package codedriver.framework.message.core
 * @Description: 消息处理器接口
 * @Author: linbq
 * @Date: 2020/12/30 15:09
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public interface IMessageHandler {
    /**
     * @Description:
     * @Author: linbq
     * @Date: 2021/1/7 11:45
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
    default String getHandler() {
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
    void send(NotifyVo notifyVo);
}
