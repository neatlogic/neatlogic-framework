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
