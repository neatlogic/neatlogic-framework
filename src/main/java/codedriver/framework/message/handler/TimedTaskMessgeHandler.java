/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.message.handler;

import codedriver.framework.message.constvalue.PopUpType;
import codedriver.framework.message.core.MessageHandlerBase;
import codedriver.framework.notify.dto.NotifyVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 定时任务消息处理器
 * @author: linbq
 * @since: 2021/4/8 19:01
 **/
@Service
public class TimedTaskMessgeHandler extends MessageHandlerBase {
    /**
     * @Description: 消息类型名称
     * @Author: linbq
     * @Date: 2021/1/7 11:45
     * @Params:[]
     * @Returns:java.lang.String
     **/
    @Override
    public String getName() {
        return "定时任务消息处理器";
    }

    /**
     * @Description: 消息类型描述
     * @Author: linbq
     * @Date: 2020/12/30 15:11
     * @Params:[]
     * @Returns:java.lang.String
     **/
    @Override
    public String getDescription() {
        return "定时任务发送消息时使用该处理器处理消息";
    }

    /**
     * @Description: 该类型消息是否需要压缩
     * @Author: linbq
     * @Date: 2021/1/13 11:36
     * @Params:[]
     * @Returns:boolean
     **/
    @Override
    public boolean getNeedCompression() {
        return false;
    }

    /**
     * @param notifyVoList
     * @Description: 将多条消息压缩成一条消息
     * @Author: linbq
     * @Date: 2021/1/13 11:37
     * @Params:[notifyVoList]
     * @Returns:codedriver.framework.notify.dto.NotifyVo
     */
    @Override
    public NotifyVo compress(List<NotifyVo> notifyVoList) {
        return null;
    }

    @Override
    public boolean isPublic(){
        return false;
    }

    @Override
    public String getPopUp(){
        return PopUpType.LONGSHOW.getValue();
    }
}
