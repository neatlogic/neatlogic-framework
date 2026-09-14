/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.globallock.dao.mapper;

import org.apache.ibatis.annotations.Param;
import neatlogic.framework.globallock.constvalue.GlobalLockOperationStatus;
import java.util.Map;

/** 按租户保存进度，以短事务更新，网络调用期间不持有进度行锁。 */
public interface GlobalLockOperationMapper {
    /** 写入操作初始状态，枚举值保持数据库协议兼容。 */
    void insert(@Param("id") String id, @Param("user") String user, @Param("lockId") Long lockId,
                @Param("action") String action, @Param("content") String content, @Param("state") GlobalLockOperationStatus state);
    Map<String, Object> get(@Param("id") String id, @Param("user") String user);
    /** 仅从预期状态原子迁移，保证一次执行权。 */
    int claim(@Param("id") String id, @Param("user") String user, @Param("lockId") Long lockId,
              @Param("action") String action, @Param("expectedState") GlobalLockOperationStatus expectedState,
              @Param("state") GlobalLockOperationStatus state);
    /** 保存操作状态及进度，数据库存储枚举对应的协议值。 */
    void update(@Param("id") String id, @Param("state") GlobalLockOperationStatus state, @Param("content") String content);
    void purge();
}
