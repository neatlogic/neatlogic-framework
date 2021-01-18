package codedriver.framework.systemnotice.login.handler;

import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.common.constvalue.UserType;
import codedriver.framework.dao.mapper.UserMapper;
import codedriver.framework.login.core.LoginPostProcessorBase;
import codedriver.framework.systemnotice.dao.mapper.SystemNoticeMapper;
import codedriver.framework.systemnotice.dto.SystemNoticeUserVo;
import codedriver.framework.systemnotice.dto.SystemNoticeVo;
import codedriver.framework.transaction.util.TransactionUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: LoginPullSystemNoticeProcessor
 * @Package codedriver.framework.message.login.handler
 * @Description: 登录后拉取系统公告处理器
 * @Author: laiwt
 * @Date: 2021/1/15 15:38
 * Copyright(c) 2020 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 **/
@Service
public class LoginPullSystemNoticeProcessor extends LoginPostProcessorBase {

    @Autowired
    private SystemNoticeMapper systemNoticeMapper;

    @Autowired
    private UserMapper userMapper;


    @Override
    protected void myLoginAfterInitialization() {

        List<String> uuidList = new ArrayList<>();
        uuidList.add(UserContext.get().getUserUuid(true));
        uuidList.add(UserType.ALL.getValue());
        uuidList.addAll(userMapper.getTeamUuidListByUserUuid(UserContext.get().getUserUuid(true)));
        uuidList.addAll(userMapper.getRoleUuidListByUserUuid(UserContext.get().getUserUuid(true)));

        /** 清理ystem_notice_user中【不存在的公告】记录 **/
        systemNoticeMapper.deleteNotExistsNoticeByUserUuid(UserContext.get().getUserUuid(true));

        /** 检查是否存在【已发布却到了失效时间的】公告，如果有，则停用 **/
        List<SystemNoticeVo> expiredNoticeList = systemNoticeMapper.getExpiredNoticeListByRecipientUuidList(uuidList);
        if (CollectionUtils.isNotEmpty(expiredNoticeList)) {
            TransactionStatus transactionStatus = TransactionUtil.openTx();
            for (SystemNoticeVo vo : expiredNoticeList) {
                vo.setStatus(SystemNoticeVo.Status.STOPPED.getValue());
                systemNoticeMapper.updateSystemNotice(vo);
            }
            TransactionUtil.commitTx(transactionStatus);
        }


        /** 在system_notice_user插入【当前用户可看的】、【已发布的】公告 **/
        List<Long> issuedNoticeList = systemNoticeMapper.getNoticeIdListByRecipientUuidList(uuidList);
        if (CollectionUtils.isNotEmpty(issuedNoticeList)) {
            List<SystemNoticeUserVo> noticeUserVoList = new ArrayList<>();
            for (Long id : issuedNoticeList) {
                noticeUserVoList.add(new SystemNoticeUserVo(id, UserContext.get().getUserUuid(true)));
            }
            systemNoticeMapper.batchInsertSystemNoticeUser(noticeUserVoList);
        }

        /**
         * 检查是否存在【当前用户可看的】、【到了生效时间，状态却还是未发布的】公告，如果有，则下发给自己
         * 其他的通知用户，如果在线则由其定时拉取，如果离线则登录时自己拉取
         **/
        List<SystemNoticeVo> hasBeenActiveNoticeList = systemNoticeMapper.getHasBeenActiveNoticeListByRecipientUuidList(uuidList);
        if (CollectionUtils.isNotEmpty(hasBeenActiveNoticeList)) {
            TransactionStatus transactionStatus = TransactionUtil.openTx();
            /** 先锁定这些公告 **/
            systemNoticeMapper.getSystemNoticeLockByIdList(hasBeenActiveNoticeList.stream().map(SystemNoticeVo::getId).collect(Collectors.toList()));
            List<SystemNoticeUserVo> currentUserNoticeList = new ArrayList<>();
            /** 更改这些公告的状态为已发布 **/
            for (SystemNoticeVo vo : hasBeenActiveNoticeList) {
                vo.setStatus(SystemNoticeVo.Status.ISSUED.getValue());
                systemNoticeMapper.updateSystemNotice(vo);
                currentUserNoticeList.add(new SystemNoticeUserVo(vo.getId(), UserContext.get().getUserUuid(true)));
            }
            TransactionUtil.commitTx(transactionStatus);
            /** 发送给当前用户 **/
            if (CollectionUtils.isNotEmpty(currentUserNoticeList)) {
                systemNoticeMapper.batchInsertSystemNoticeUser(currentUserNoticeList);
            }

        }


    }

}
