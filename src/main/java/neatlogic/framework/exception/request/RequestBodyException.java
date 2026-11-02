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

package neatlogic.framework.exception.request;

import neatlogic.framework.exception.core.ApiRuntimeException;

/** 请求读取失败；携带独立 HTTP 状态，避免被认证错误或业务异常响应覆盖。 */
public class RequestBodyException extends ApiRuntimeException {
    private static final long serialVersionUID = 1L;
    private final int status;

    /** 限制名称只使用服务端固定标识，不包含字段内容或认证信息。 */
    public RequestBodyException(String limit, long maximum, long actual) {
        super("framework.requestbody.limit", limit, maximum, actual);
        status = 413;
    }

    /** 解析和存储错误保留原始原因，对外展示操作、URI 和异常类型，避免泄漏报文内容。 */
    public RequestBodyException(int status, String operation, String uri, Throwable cause) {
        super("framework.requestbody.readfailed", cause, operation, uri,
                cause == null ? operation : cause.getClass().getSimpleName());
        this.status = status;
    }

    public int getStatus() { return status; }
}
