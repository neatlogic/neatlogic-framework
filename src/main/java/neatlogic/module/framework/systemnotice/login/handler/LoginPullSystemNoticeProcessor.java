/*
 * Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.module.framework.systemnotice.login.handler;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.common.constvalue.systemuser.SystemUserFactory;
import neatlogic.framework.login.core.LoginPostProcessorBase;
import neatlogic.module.framework.systemnotice.service.SystemNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Title: LoginPullSystemNoticeProcessor
 * @Package neatlogic.framework.message.login.handler
 * @Description: 登录后拉取系统公告处理器
 * @Author: laiwt
 * @Date: 2021/1/15 15:38
 **/
@Service
public class LoginPullSystemNoticeProcessor extends LoginPostProcessorBase {

    @Autowired
    private SystemNoticeService systemNoticeService;

    @Override
    protected void myLoginAfterInitialization() {
        // 系统用户登录了，不需要拉取系统通知
        if (SystemUserFactory.getUserVoByUser(UserContext.get().getUserUuid()) != null) {
            return;
        }
        /** 清理掉system_notice_user中因删除公告或更改公告通知对象而遗留的记录 **/
//        systemNoticeService.clearSystemNoticeUser();

        /** 检查是否存在【已下发却到了失效时间的】公告，如果有，则停用 **/
//        systemNoticeService.stopExpiredSystemNotice();

        /** 在system_notice_user插入【当前用户可看的】、【已下发的】公告 **/
        systemNoticeService.pullIssuedSystemNotice();

        /**
         * 检查是否存在【当前用户可看的】、【到了生效时间，却还没下发】公告，如果有，则下发给当前用户
         * 其他的通知用户，如果在线则由前端定时拉取，如果离线则登录时拉取
         **/
//        systemNoticeService.pullActiveSystemNotice();
    }

}
