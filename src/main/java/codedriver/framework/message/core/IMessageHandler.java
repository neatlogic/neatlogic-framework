package codedriver.framework.message.core;

import codedriver.framework.message.constvalue.PopUpType;
import codedriver.framework.notify.dto.NotifyVo;
import org.springframework.util.ClassUtils;

import java.util.List;

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
     * @Description: 消息类型名称
     * @Author: linbq
     * @Date: 2021/1/7 11:45
     * @Params:[]
     * @Returns:java.lang.String
     **/
    public String getName();

    /**
     * @Description: 消息类型处理器全类名
     * @Author: linbq
     * @Date: 2020/12/30 15:11
     * @Params:[]
     * @Returns:java.lang.String
     **/
    public default String getHandler() {
        return ClassUtils.getUserClass(this.getClass()).getSimpleName();
    }

    /**
     * @Description: 消息类型描述
     * @Author: linbq
     * @Date: 2020/12/30 15:11
     * @Params:[]
     * @Returns:java.lang.String
     **/
    public String getDescription();

    /**
     * @Description: 发送消息
     * @Author: linbq
     * @Date: 2020/12/30 15:11
     * @Params:[messageVo]
     * @Returns:void
     **/
    public void send(NotifyVo notifyVo);

    /**
     * @Description: 该类型消息是否需要压缩
     * @Author: linbq
     * @Date: 2021/1/13 11:36
     * @Params:[]
     * @Returns:boolean
     **/
    public boolean getNeedCompression();

    /**
     * @Description: 将多条消息压缩成一条消息
     * @Author: linbq
     * @Date: 2021/1/13 11:37
     * @Params:[notifyVoList]
     * @Returns:codedriver.framework.notify.dto.NotifyVo
     **/
    public NotifyVo compress(List<NotifyVo> notifyVoList);

    /**
     * 是否公开给用户修改配置，默认公开
     * @return
     */
    public default boolean isPublic(){
        return true;
    }

    /**
     * 弹框方式，默认关闭
     * @return
     */
    public default String getPopUp(){
        return PopUpType.CLOSE.getValue();
    }
}
