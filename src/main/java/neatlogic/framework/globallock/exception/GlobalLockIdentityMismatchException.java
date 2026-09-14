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

package neatlogic.framework.globallock.exception;

import neatlogic.framework.dto.globallock.GlobalLockVo;
import neatlogic.framework.exception.core.ApiRuntimeException;

/** 同一锁 ID 的资源或执行身份冲突，仅展示冲突字段，不输出完整业务参数。 */
public class GlobalLockIdentityMismatchException extends ApiRuntimeException {
    /** 记录冲突字段的原值和请求值，由处理器提供业务字段名称。 */
    public GlobalLockIdentityMismatchException(GlobalLockVo existing, GlobalLockVo request,
                                              String field, Object existingValue, Object requestedValue) {
        super("globallock.error.identitymismatch", request.getId(), field, existingValue, requestedValue,
                existing.getKey(), request.getKey(), existing.getHandler(), request.getHandler());
    }
}
