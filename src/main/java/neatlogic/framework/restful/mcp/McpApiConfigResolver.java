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

package neatlogic.framework.restful.mcp;

import java.util.Objects;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiType;

/** 统一解析 MCP 有效配置；不读取数据库、不改变共享注册元数据。 */
public final class McpApiConfigResolver {
    private McpApiConfigResolver() { }

    /** 页面明确保存的开关优先，缺少保存值时采用系统声明。 */
    public static Integer resolveIsMcp(ApiVo system, ApiVo saved) {
        return saved != null && saved.getIsMcp() != null ? saved.getIsMcp()
                : system != null && system.getIsMcp() != null ? system.getIsMcp() : 0;
    }

    /** 生成请求级配置副本，沿用页面的启停、审计和限流设置。 */
    public static ApiVo merge(ApiVo system, ApiVo saved) throws CloneNotSupportedException {
        if (system == null) return saved == null ? null : saved.clone();
        ApiVo effective = system.clone();
        effective.setIsMcp(resolveIsMcp(system, saved));
        if (saved != null) {
            effective.setIsActive(saved.getIsActive());
            effective.setNeedAudit(saved.getNeedAudit());
            effective.setQps(saved.getQps());
        }
        return effective;
    }

    /** 默认开放不绕过接口类型、模块注册或接口启停约束。 */
    public static boolean isAvailable(ApiVo system, ApiVo saved) {
        return system != null && Objects.equals(ApiType.OBJECT.getValue(), system.getType())
                && Objects.equals(1, resolveIsMcp(system, saved))
                && Objects.equals(1, saved == null ? system.getIsActive() : saved.getIsActive());
    }
}
