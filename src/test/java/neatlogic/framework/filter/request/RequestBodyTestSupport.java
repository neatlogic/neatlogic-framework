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

package neatlogic.framework.filter.request;

import neatlogic.framework.filter.CachedBodyHttpServletRequest;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;

/** 测试专用的缓存观测工具，避免为了断言向生产类增加状态或文件访问接口。 */
final class RequestBodyTestSupport {
    /** 仅提供静态观测方法。 */
    private RequestBodyTestSupport() { }

    /** 读取已有缓存状态，不触发 body 读取或缓存初始化。 */
    static boolean isBodyCached(RequestBodyResources resources) {
        return cache(resources).isCached();
    }

    /** 取得落盘文件用于阈值和清理断言；未初始化或仍在内存时返回 null。 */
    static File getBodyCacheFile(RequestBodyResources resources) {
        DeferredFileOutputStream storage = (DeferredFileOutputStream) ReflectionTestUtils.getField(cache(resources), "storage");
        return storage == null ? null : storage.getFile();
    }

    /** 只在测试源码中访问内部资源，不改变生产类的可见性。 */
    private static CachedBodyHttpServletRequest cache(RequestBodyResources resources) {
        return (CachedBodyHttpServletRequest) ReflectionTestUtils.getField(resources, "cache");
    }
}
