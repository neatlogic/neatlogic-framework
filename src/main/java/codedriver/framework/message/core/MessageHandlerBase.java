package codedriver.framework.message.core;

import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.GroupSearch;
import codedriver.framework.dao.mapper.TeamMapper;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.dto.UserSessionVo;
import codedriver.framework.message.dao.mapper.MessageMapper;
import codedriver.framework.message.delay.MessageCache;
import codedriver.framework.message.dto.MessageRecipientVo;
import codedriver.framework.message.dto.MessageSearchVo;
import codedriver.framework.message.dto.MessageVo;
import codedriver.framework.notify.dto.NotifyReceiverVo;
import codedriver.framework.notify.dto.NotifyVo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Title: MessageHandlerBase
 * @Package codedriver.framework.message.core
 * @Description: 消息处理器基类
 * @Author: linbq
 * @Date: 2020/12/30 17:25
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
public abstract class MessageHandlerBase implements IMessageHandler {

    private static MessageMapper messageMapper;
    private static UserMapper userMapper;

    @Autowired
    public void setMessageMapper(MessageMapper _messageMapper){
        messageMapper = _messageMapper;
    }
    @Autowired
    public void setUserMapper(UserMapper _userMapper){
        userMapper = _userMapper;
    }

    @Override
    public void send(NotifyVo notifyVo) {
        MessageCache.add(notifyVo);
    }
}
