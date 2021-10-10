/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.message.thread;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.message.dao.mapper.MessageMapper;
import codedriver.framework.message.dto.MessageSearchVo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class UpdateMessageUserIsShowThread extends CodeDriverThread {

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
        /* 将is_show=1,expired_time>=NOW(3) （临时弹窗且已自动消失）的消息改成 is_show = 2, expired_time = null **/
        messageMapper.updateMessageUserExpiredIsShow1To2AndExpiredTimeIsNullByUserUuid(userUuid);
        MessageSearchVo messageSearchVo = new MessageSearchVo();
        messageSearchVo.setUserUuid(userUuid);
        if (CollectionUtils.isNotEmpty(popUpCloseHandlerList)) {
            /* 将不用弹窗的消息状态is_show由 0 改成 2 **/
            messageSearchVo.setHandlerList(popUpCloseHandlerList);
            messageSearchVo.setIsShow(2);
            messageMapper.updateMessageUserIsShowAndExpiredTimeByUserUuidAndHandlerList(messageSearchVo);
        }
        if (longShowCount != 0 && CollectionUtils.isNotEmpty(popUpLongShowHandlerList)) {
            /* 将持续弹窗的消息状态is_show由 0 改成 1 **/
            messageSearchVo.setHandlerList(popUpLongShowHandlerList);
            messageSearchVo.setIsShow(1);
            messageMapper.updateMessageUserIsShowAndExpiredTimeByUserUuidAndHandlerList(messageSearchVo);
        }
        if (shortShowCount != 0 && CollectionUtils.isNotEmpty(popUpShortShowHandlerList)) {
            /* 将临时弹窗的消息状态is_show由 0 改成 1，同时设置失效时间 **/
            messageSearchVo.setHandlerList(popUpShortShowHandlerList);
            messageSearchVo.setIsShow(1);
            messageSearchVo.setExpiredTime(expiredTime);
            messageMapper.updateMessageUserIsShowAndExpiredTimeByUserUuidAndHandlerList(messageSearchVo);
        }
    }
}
