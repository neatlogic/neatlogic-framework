/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.message.thread;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.message.dao.mapper.MessageMapper;
import neatlogic.framework.message.dto.MessageSearchVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UpdateMessageUserIsShowThread extends NeatLogicThread {

    private static MessageMapper messageMapper;

    @Resource
    public void setMessageMapper(MessageMapper _messageMapper) {
        messageMapper = _messageMapper;
    }

    private int shortShowCount;
    private int longShowCount;
    private int unreadCount;
    private List<String> popUpShortShowHandlerList;
    private List<String> popUpLongShowHandlerList;
    private List<String> popUpCloseHandlerList;
    private Date expiredTime;

    public UpdateMessageUserIsShowThread() {
        super("MESSAGE-USER-ISSHOW-UPDATER");
    }

    public UpdateMessageUserIsShowThread(
            int shortShowCount,
            int longShowCount,
            List<String> popUpShortShowHandlerList,
            List<String> popUpLongShowHandlerList,
            List<String> popUpCloseHandlerList,
            Date expiredTime) {
        super("MESSAGE-USER-ISSHOW-UPDATER");
        this.shortShowCount = shortShowCount;
        this.longShowCount = longShowCount;
        this.popUpShortShowHandlerList = popUpShortShowHandlerList;
        this.popUpLongShowHandlerList = popUpLongShowHandlerList;
        this.popUpCloseHandlerList = popUpCloseHandlerList;
        this.expiredTime = expiredTime;
    }

    @Override
    protected void execute() {
        String userUuid = UserContext.get().getUserUuid(true);
        /* ???is_show=1,expired_time>=NOW(3) ??????????????????????????????????????????????????? is_show = 2, expired_time = null **/
        messageMapper.updateMessageUserExpiredIsShow1To2AndExpiredTimeIsNullByUserUuid(userUuid);
        MessageSearchVo messageSearchVo = new MessageSearchVo();
        messageSearchVo.setUserUuid(userUuid);
        if (CollectionUtils.isNotEmpty(popUpCloseHandlerList)) {
            /* ??????????????????????????????is_show??? 0 ?????? 2 **/
            messageSearchVo.setHandlerList(popUpCloseHandlerList);
            messageSearchVo.setIsShow(2);
            messageMapper.updateMessageUserIsShowAndExpiredTimeByUserUuidAndHandlerList(messageSearchVo);
        }
        if (longShowCount != 0 && CollectionUtils.isNotEmpty(popUpLongShowHandlerList)) {
            /* ??????????????????????????????is_show??? 0 ?????? 1 **/
            messageSearchVo.setHandlerList(popUpLongShowHandlerList);
            messageSearchVo.setIsShow(1);
            messageMapper.updateMessageUserIsShowAndExpiredTimeByUserUuidAndHandlerList(messageSearchVo);
        }
        if (shortShowCount != 0 && CollectionUtils.isNotEmpty(popUpShortShowHandlerList)) {
            /* ??????????????????????????????is_show??? 0 ?????? 1??????????????????????????? **/
            messageSearchVo.setHandlerList(popUpShortShowHandlerList);
            messageSearchVo.setIsShow(1);
            messageSearchVo.setExpiredTime(expiredTime);
            messageMapper.updateMessageUserIsShowAndExpiredTimeByUserUuidAndHandlerList(messageSearchVo);
        }
    }
}
