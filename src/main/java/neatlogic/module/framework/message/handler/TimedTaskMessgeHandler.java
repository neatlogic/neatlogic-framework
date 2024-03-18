/*Copyright (C) $today.year  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.module.framework.message.handler;

import neatlogic.framework.message.constvalue.PopUpType;
import neatlogic.framework.message.core.MessageHandlerBase;
import neatlogic.framework.notify.dto.NotifyVo;
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
     * @Returns:neatlogic.framework.notify.dto.NotifyVo
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
