/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

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
