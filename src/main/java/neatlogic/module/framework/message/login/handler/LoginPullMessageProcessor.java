/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.module.framework.message.login.handler;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.util.PageUtil;
import neatlogic.framework.dto.AuthenticationInfoVo;
import neatlogic.framework.login.core.LoginPostProcessorBase;
import neatlogic.framework.message.core.MessageHandlerFactory;
import neatlogic.framework.message.dao.mapper.MessageMapper;
import neatlogic.framework.message.dto.MessageHandlerVo;
import neatlogic.framework.message.dto.MessageSearchVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LoginPullMessageProcessor extends LoginPostProcessorBase {

    @Resource
    private MessageMapper messageMapper;

    @Override
    protected void myLoginAfterInitialization() {
        MessageSearchVo searchVo = new MessageSearchVo();
        List<String> handlerList = getActiveHandlerList();
        if(CollectionUtils.isNotEmpty(handlerList)){
            /** 部分订阅**/
            searchVo.setHandlerList(handlerList);
        }else if(handlerList != null){
            /** 全部不订阅**/
            return;
        }

        Long messageId = getMinMessageId();
        if(messageId == null){
            return;
        }
        searchVo.setMessageId(messageId);

        searchVo.setPageSize(1000);
        searchVo.setUserUuid(UserContext.get().getUserUuid(true));
        AuthenticationInfoVo authenticationInfoVo = UserContext.get().getAuthenticationInfoVo();
        searchVo.setRoleUuidList(authenticationInfoVo.getRoleUuidList());
        searchVo.setTeamUuidList(authenticationInfoVo.getTeamUuidList());

        int rowNum = messageMapper.getMessagePullCount(searchVo);
        if(rowNum > 0){
            int pageCount = PageUtil.getPageCount(rowNum, searchVo.getPageSize());
            for (int i = 0; i < pageCount; i++) {
                List<Long> messageIdList = messageMapper.getMessagePullList(searchVo);
                insertMessageUserList(messageIdList);
                searchVo.setMessageId(messageIdList.get(messageIdList.size() - 1));
            }
        }
    }
    /**
     * @Description: 返回已订阅的handler列表，如果全部订阅则返回null,如果全部不订阅则返回空列表[]
     * @Author: linbq
     * @Date: 2021/1/6 16:56
     * @Params:[]
     * @Returns:java.util.List<java.lang.String>
     **/
    private List<String> getActiveHandlerList(){
        Map<String, MessageHandlerVo> messageSubscribeMap = new HashMap<>();
        List<String> unActiveHandlerList = new ArrayList<>();
        List<MessageHandlerVo> messageSubscribeList = messageMapper.getMessageSubscribeListByUserUuid(UserContext.get().getUserUuid(true));
        for (MessageHandlerVo messageSubscribe : messageSubscribeList) {
            messageSubscribeMap.put(messageSubscribe.getHandler(), messageSubscribe);
            if (messageSubscribe.getIsActive() == 0) {
                unActiveHandlerList.add(messageSubscribe.getHandler());
            }
        }
        if (CollectionUtils.isNotEmpty(unActiveHandlerList)) {
            List<String> handlerList = MessageHandlerFactory.getMessageHandlerVoList().stream().map(MessageHandlerVo::getHandler).collect(Collectors.toList());
            handlerList.removeAll(unActiveHandlerList);
            return handlerList;
        }
        return null;
    }

    /**
     * @Description: 根据此次拉取信息的最早发送时间，推算出此次拉取信息的最小id
     * @Author: linbq
     * @Date: 2021/1/6 17:05
     * @Params:[]
     * @Returns:java.lang.Long
     **/
    private Long getMinMessageId(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - Config.NEW_MESSAGE_EXPIRED_DAY());
        Date earliestSendingTime = calendar.getTime();

        Date lastPullTime = null;
        Long maxMessageId = messageMapper.getMessageUserMaxMessageIdByUserUuid(UserContext.get().getUserUuid(true));
        if (maxMessageId != null) {
            lastPullTime = messageMapper.getMessageInsertTimeById(maxMessageId);
        }
        if (lastPullTime == null || lastPullTime.before(earliestSendingTime)) {
            return messageMapper.getMessageMaxIdByLessThanInsertTime(earliestSendingTime);
        } else {
            return maxMessageId;
        }
    }
    /**
     * @Description: 保存用户拉取到的新消息id
     * @Author: linbq
     * @Date: 2021/1/5 14:36
     * @Params:[messageIdList]
     * @Returns:void
     **/
    private void insertMessageUserList(List<Long> messageIdList) {
        int size = Math.min(1000, messageIdList.size());
        List<MessageSearchVo> messageSearchVoList = new ArrayList<>(size);
        String userUuid = UserContext.get().getUserUuid(true);
        for (Long messageId : messageIdList) {
            messageSearchVoList.add(new MessageSearchVo(userUuid, messageId));
            if (messageSearchVoList.size() == 1000) {
                messageMapper.insertMessageUser(messageSearchVoList);
                messageSearchVoList.clear();
            }
        }
        if (CollectionUtils.isNotEmpty(messageSearchVoList)) {
            messageMapper.insertMessageUser(messageSearchVoList);
        }
    }
}
