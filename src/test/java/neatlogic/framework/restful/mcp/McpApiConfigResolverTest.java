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

import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiType;
import org.junit.Assert;
import org.junit.Test;

/** 验证系统默认与用户覆盖的语义，不依赖租户数据库。 */
public class McpApiConfigResolverTest {
    /** 默认接口无需数据库记录即可使用，未声明的接口保持关闭。 */
    @Test public void usesSystemDefaultWithoutSavedConfiguration() {
        ApiVo system = api(1);
        Assert.assertTrue(McpApiConfigResolver.isAvailable(system, null));
        system.setIsMcp(0);
        Assert.assertFalse(McpApiConfigResolver.isAvailable(system, null));
    }
    /** 用户关闭优先于系统默认，显式开启也能覆盖未声明接口。 */
    @Test public void savedSelectionAlwaysWins() {
        Assert.assertFalse(McpApiConfigResolver.isAvailable(api(1), api(0)));
        Assert.assertTrue(McpApiConfigResolver.isAvailable(api(0), api(1)));
    }
    /** 默认开放不能绕过接口停用或非对象协议。 */
    @Test public void respectsActivationAndApiType() {
        ApiVo system = api(1); ApiVo saved = api(1); saved.setIsActive(0);
        Assert.assertFalse(McpApiConfigResolver.isAvailable(system, saved));
        system.setType("binarystream");
        Assert.assertFalse(McpApiConfigResolver.isAvailable(system, null));
    }
    /** 合并不污染注册对象，用户开关、审计和限流在请求副本中保留。 */
    @Test public void mergesIntoIsolatedRequestCopy() throws Exception {
        ApiVo system = api(1); ApiVo saved = api(0); saved.setQps(5D); saved.setNeedAudit(1);
        ApiVo effective = McpApiConfigResolver.merge(system, saved);
        Assert.assertNotSame(system, effective);
        Assert.assertEquals(Integer.valueOf(1), system.getIsMcp());
        Assert.assertEquals(Integer.valueOf(0), effective.getIsMcp());
        Assert.assertEquals(saved.getQps(), effective.getQps());
        Assert.assertEquals(saved.getNeedAudit(), effective.getNeedAudit());
    }
    /** 创建最小有效对象接口元数据。 */
    private ApiVo api(int enabled) {
        ApiVo api = new ApiVo(); api.setIsMcp(enabled); api.setIsActive(1);
        api.setType(ApiType.OBJECT.getValue()); return api;
    }
}
