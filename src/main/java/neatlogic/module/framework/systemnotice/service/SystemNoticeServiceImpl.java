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

package neatlogic.module.framework.systemnotice.service;

import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.common.constvalue.GroupSearch;
import neatlogic.framework.common.constvalue.UserType;
import neatlogic.framework.common.dto.BasePageVo;
import neatlogic.framework.common.util.PageUtil;
import neatlogic.framework.dao.mapper.RoleMapper;
import neatlogic.framework.dao.mapper.TeamMapper;
import neatlogic.framework.dao.mapper.UserSessionMapper;
import neatlogic.framework.scheduler.core.IJob;
import neatlogic.framework.scheduler.core.SchedulerManager;
import neatlogic.framework.scheduler.dto.JobObject;
import neatlogic.framework.scheduler.exception.ScheduleHandlerNotFoundException;
import neatlogic.framework.service.UserService;
import neatlogic.framework.systemnotice.dao.mapper.SystemNoticeMapper;
import neatlogic.framework.systemnotice.dto.SystemNoticeRecipientVo;
import neatlogic.framework.systemnotice.dto.SystemNoticeUserVo;
import neatlogic.framework.systemnotice.dto.SystemNoticeVo;
import neatlogic.framework.systemnotice.service.ISystemNoticeCrossoverService;
import neatlogic.module.framework.systemnotice.schedule.StopSystemNoticeJob;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Title: SystemNoticeServiceImpl
 * @Package: neatlogic.framework.systemnotice.service
 * @Description:
 * @Author: laiwt
 * @Date: 2021/1/20 11:02
 **/
@Service
public class SystemNoticeServiceImpl implements SystemNoticeService, ISystemNoticeCrossoverService {

    @Autowired
    private SystemNoticeMapper systemNoticeMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private TeamMapper teamMapper;

    @Resource
    private UserSessionMapper userSessionMapper;

    @Resource
    private SchedulerManager schedulerManager;

    @Resource
    private UserService userService;

    @Override
    public void clearSystemNoticeUser() {
        List<String> recipientUuidList = getRecipientUuidList();
        Set<Long> noticeIdList = new HashSet<>();
        /** 获取system_notice_user中，因公告被删除而残留的记录 **/
        noticeIdList.addAll(systemNoticeMapper.getNotExistsNoticeIdListFromNoticeUserByUserUuid(UserContext.get().getUserUuid(true)));
        /** 获取system_notice_user中，因更改公告通知对象而残留的记录  **/
        noticeIdList.addAll(systemNoticeMapper.getNotInNoticeScopeNoticeIdListByUserUuid(recipientUuidList, UserContext.get().getUserUuid(true)));
        /** 清理掉上述两种记录 **/
        if (CollectionUtils.isNotEmpty(noticeIdList)) {
            systemNoticeMapper.deleteSystemNoticeUserByUserUuid(UserContext.get().getUserUuid(true), noticeIdList);
        }
    }

    @Override
    public void stopExpiredSystemNotice() {
        List<String> recipientUuidList = getRecipientUuidList();
        List<SystemNoticeVo> expiredNoticeList = systemNoticeMapper.getExpiredNoticeListByRecipientUuidList(recipientUuidList);
        if (CollectionUtils.isNotEmpty(expiredNoticeList)) {
            for (SystemNoticeVo vo : expiredNoticeList) {
                systemNoticeMapper.stopSystemNoticeById(vo);
            }
        }
    }

    @Override
    public void pullIssuedSystemNotice() {
        String userUuid = UserContext.get().getUserUuid(true);
        List<String> recipientUuidList = getRecipientUuidList();
        List<Long> issuedNoticeList = systemNoticeMapper.getIssuedNoticeIdListByUserUuidAndRecipientUuidList(userUuid, recipientUuidList);
        if (CollectionUtils.isNotEmpty(issuedNoticeList)) {
            List<SystemNoticeUserVo> noticeUserVoList = new ArrayList<>();
            for (Long id : issuedNoticeList) {
                noticeUserVoList.add(new SystemNoticeUserVo(id, userUuid));
            }
            systemNoticeMapper.batchInsertSystemNoticeUser(noticeUserVoList);
        }
//        String userUuid = UserContext.get().getUserUuid();
//        List<String> uuidList = UserContext.get().getUuidList();
//        uuidList.add(userUuid);
//        uuidList.add(UserType.ALL.getValue());
//        systemNoticeMapper.insertInsertSystemNoticeUser(userUuid, uuidList);
    }

    @Override
    public void pullActiveSystemNotice() {
        List<String> recipientUuidList = getRecipientUuidList();
        List<SystemNoticeVo> hasBeenActiveNoticeList = systemNoticeMapper.getHasBeenActiveNoticeListByRecipientUuidList(recipientUuidList);
        if (CollectionUtils.isNotEmpty(hasBeenActiveNoticeList)) {
            List<SystemNoticeUserVo> currentUserNoticeList = new ArrayList<>();
            /** 更改这些公告的状态为已下发 **/
            for (SystemNoticeVo vo : hasBeenActiveNoticeList) {
                vo.setStatus(SystemNoticeVo.Status.ISSUED.getValue());
                vo.setIssueTime(vo.getStartTime());
                systemNoticeMapper.updateSystemNoticeStatus(vo);
                currentUserNoticeList.add(new SystemNoticeUserVo(vo.getId(), UserContext.get().getUserUuid(true)));
                /** 如果没有忽略已读，那么更改is_read为0 **/
                if (vo.getIgnoreRead() != null && vo.getIgnoreRead() == 0) {
                    systemNoticeMapper.updateSystemNoticeUserReadStatus(vo.getId(), UserContext.get().getUserUuid(true), 0);
                }
            }
            /** 发送给当前用户 **/
            if (CollectionUtils.isNotEmpty(currentUserNoticeList)) {
                systemNoticeMapper.batchInsertSystemNoticeUser(currentUserNoticeList);
            }
        }
    }

    @Override
    public boolean issueSystemNotice(SystemNoticeVo vo) {
        Integer PAGE_SIZE = 100;
        /* 如果没有忽略已读，则把system_notice_user中的is_read设为0 **/
        if (vo.getIgnoreRead() != null && vo.getIgnoreRead() == 0) {
            /* 经测试，该语句update 53万条数据耗时约1.2s，故不单独开线程执行 **/
            systemNoticeMapper.updateReadStatusToNotReadByNoticeId(vo.getId());
        } else {
            systemNoticeMapper.deleteSystemNoticeUserByNoticeId(vo.getId());
        }

        List<SystemNoticeRecipientVo> recipientList = systemNoticeMapper.getRecipientListByNoticeId(vo.getId());
        if (CollectionUtils.isNotEmpty(recipientList)) {
            long expireTime = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(Config.USER_EXPIRETIME());
            if (recipientList.stream().anyMatch(o -> UserType.ALL.getValue().equals(o.getUuid()))) {
                /* 如果通知范围是所有人，那么找出当前所有的在线用户 **/
                int allOnlineUserCount = userSessionMapper.getAllOnlineUserCount(new Date(expireTime));
                if (allOnlineUserCount > 0) {
                    CachedThreadPool.execute(new NeatLogicThread("NOTICE-INSERTER") {
                        @Override
                        protected void execute() {
                            Date expireDate = new Date(expireTime);
                            BasePageVo pageVo = new BasePageVo();
                            pageVo.setPageSize(PAGE_SIZE);
                            pageVo.setPageCount(PageUtil.getPageCount(allOnlineUserCount, pageVo.getPageSize()));
                            List<SystemNoticeUserVo> noticeUserVoList = new ArrayList<>();
                            for (int i = 1; i <= pageVo.getPageCount(); i++) {
                                pageVo.setCurrentPage(i);
                                List<String> allOnlineUser = userSessionMapper.getAllOnlineUser(expireDate, pageVo.getStartNum(), pageVo.getPageSize());
                                if (CollectionUtils.isNotEmpty(allOnlineUser)) {
                                    allOnlineUser.forEach(o -> noticeUserVoList.add(new SystemNoticeUserVo(vo.getId(), o)));
                                    systemNoticeMapper.batchInsertSystemNoticeUser(noticeUserVoList);
                                    noticeUserVoList.clear();
                                }
                            }
                        }
                    });
                }
            } else {
                List<String> userUuidList = recipientList.stream()
                        .filter(o -> GroupSearch.USER.getValue().equals(o.getType()))
                        .map(SystemNoticeRecipientVo::getUuid)
                        .collect(Collectors.toList());
                List<String> teamUuidList = recipientList.stream()
                        .filter(o -> GroupSearch.TEAM.getValue().equals(o.getType()))
                        .map(SystemNoticeRecipientVo::getUuid)
                        .collect(Collectors.toList());
                List<String> roleUuidList = recipientList.stream()
                        .filter(o -> GroupSearch.ROLE.getValue().equals(o.getType()))
                        .map(SystemNoticeRecipientVo::getUuid)
                        .collect(Collectors.toList());
                List<String> allUserUuidlist = userService.getUserUuidListByUserUuidListAndTeamUuidListAndRoleUuidList(userUuidList, teamUuidList, roleUuidList);
                if (CollectionUtils.isNotEmpty(allUserUuidlist)) {
                    CachedThreadPool.execute(new NeatLogicThread("NOTICE-INSERTER") {
                        @Override
                        protected void execute() {
                            Date expireDate = new Date(expireTime);
                            List<SystemNoticeUserVo> noticeUserVoList = new ArrayList<>();
                            for (int fromIndex = 0; fromIndex < allUserUuidlist.size(); fromIndex += PAGE_SIZE) {
                                int toIndex = fromIndex + PAGE_SIZE;
                                if (toIndex > allUserUuidlist.size()) {
                                    toIndex = allUserUuidlist.size();
                                }
                                List<String> list = allUserUuidlist.subList(fromIndex, toIndex);
                                List<String> onlineUserList = userSessionMapper.getOnlineUserUuidListByUserUuidListAndGreaterThanSessionTime(list, expireDate);
                                if (CollectionUtils.isNotEmpty(onlineUserList)) {
                                    onlineUserList.forEach(o -> noticeUserVoList.add(new SystemNoticeUserVo(vo.getId(), o)));
                                    systemNoticeMapper.batchInsertSystemNoticeUser(noticeUserVoList);
                                    noticeUserVoList.clear();
                                }
                            }
                        }
                    });
                }
            }
        }
        if (vo.getEndTime() != null) {
            IJob jobHandler = SchedulerManager.getHandler(StopSystemNoticeJob.class.getName());
            if (jobHandler == null) {
                throw new ScheduleHandlerNotFoundException(StopSystemNoticeJob.class.getName());
            }
            String tenantUuid = TenantContext.get().getTenantUuid();
            JobObject jobObject = new JobObject.Builder(vo.getId().toString(), jobHandler.getGroupName(), jobHandler.getClassName(), tenantUuid)
                    .withBeginTime(vo.getEndTime())
                    .withIntervalInSeconds(60 * 60)
                    .withRepeatCount(0)
                    .build();
            schedulerManager.loadJob(jobObject);
        }
        return true;
    }

    private List<String> getRecipientUuidList() {
        List<String> uuidList = UserContext.get().getUuidList();
//        uuidList.add(UserContext.get().getUserUuid(true));
        uuidList.add(UserType.ALL.getValue());
//        uuidList.addAll(teamMapper.getTeamUuidListByUserUuid(UserContext.get().getUserUuid(true)));
//        uuidList.addAll(roleMapper.getRoleUuidListByUserUuid(UserContext.get().getUserUuid(true)));
        return uuidList;
    }
}
