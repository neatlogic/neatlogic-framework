package neatlogic.framework.message.core;

import neatlogic.framework.message.constvalue.PopUpType;
import neatlogic.framework.notify.dto.NotifyVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

import java.util.List;

/**
 * @Title: IMessageHandler
 * @Package neatlogic.framework.message.core
 * @Description: 消息处理器接口
 * @Author: linbq
 * @Date: 2020/12/30 15:09
 **/
public interface IMessageHandler {
    /**
     * @Description: 消息类型名称
     * @author: linbq
     * @Date: 2021/1/7 11:45
     **/
    String getName();

    /**
     * @Description: 消息类型处理器全类名
     * @Author: linbq
     * @Date: 2020/12/30 15:11
     **/
    default String getHandler() {
        return ClassUtils.getUserClass(this.getClass()).getName();
    }

    /**
     * @Description: 消息类型描述
     * @Author: linbq
     * @Date: 2020/12/30 15:11
     **/
    String getDescription();

    /**
     * @Description: 发送消息
     * @Author: linbq
     * @Date: 2020/12/30 15:11
     **/
    void send(NotifyVo notifyVo);

    /**
     * @Description: 该类型消息是否需要压缩
     * @Author: linbq
     * @Date: 2021/1/13 11:36
     **/
    boolean getNeedCompression();

    /**
     * @Description: 将多条消息压缩成一条消息
     * @Author: linbq
     * @Date: 2021/1/13 11:37
     **/
    NotifyVo compress(List<NotifyVo> notifyVoList);

    /**
     * 是否公开给用户修改配置，默认公开
     */
    default boolean isPublic(){
        return true;
    }

    /**
     * 弹框方式，默认关闭
     */
    default String getPopUp(){
        return PopUpType.CLOSE.getValue();
    }


    /**
     * 获取发起方信息，目前用于异常邮件
     * @return 发起方信息
     */
    default String getCallerMessage(NotifyVo notifyVo){
        return StringUtils.EMPTY;
    }

}
