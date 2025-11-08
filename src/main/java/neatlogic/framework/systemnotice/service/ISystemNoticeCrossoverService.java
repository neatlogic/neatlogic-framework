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

package neatlogic.framework.systemnotice.service;

import neatlogic.framework.crossover.ICrossoverService;
import neatlogic.framework.systemnotice.dto.SystemNoticeVo;

public interface ISystemNoticeCrossoverService extends ICrossoverService {

    /**
     * @Description: 清理掉system_notice_user中因删除公告或更改公告通知对象而遗留的记录
     * @Author: laiwt
     * @Date: 2021/1/20 11:10
     * @Params: []
     * @Returns: void
     **/
    public void clearSystemNoticeUser();

    /**
     * @Description: 检查是否存在【已下发却到了失效时间的】公告，如果有，则停用
     * @Author: laiwt
     * @Date: 2021/1/20 11:12
     * @Params: []
     * @Returns: void
     **/
    public void stopExpiredSystemNotice();

    /**
     * @Description: 在system_notice_user插入【当前用户可看的】、【已下发的】公告
     * @Author: laiwt
     * @Date: 2021/1/20 11:12
     * @Params: []
     * @Returns: void
     **/
    public void pullIssuedSystemNotice();

    /**
     * @Description: 检查是否存在【当前用户可看的】、【到了生效时间，却还没下发】公告，
     * 如果有则下发给当前用户
     * @Author: laiwt
     * @Date: 2021/1/20 11:13
     * @Params: []
     * @Returns: void
     **/
    public void pullActiveSystemNotice();

    /**
     * 下发公告
     * @param systemNoticeVo
     */
    boolean issueSystemNotice(SystemNoticeVo systemNoticeVo);
}
