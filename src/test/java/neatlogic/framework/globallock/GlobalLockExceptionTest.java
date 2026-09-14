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

package neatlogic.framework.globallock;

import neatlogic.framework.dto.globallock.GlobalLockVo;
import neatlogic.framework.globallock.exception.GlobalLockIdentityMismatchException;
import neatlogic.framework.globallock.exception.GlobalLockReleaseException;
import org.junit.Test;
import java.sql.SQLException;
import java.util.Locale;
import static org.junit.Assert.*;

/** 验证诊断文案保留定位字段、数据库根因及中英文翻译。 */
public class GlobalLockExceptionTest {
    /** 包装数据库异常必须保留原 cause，不能把错误码或根因藏在空消息后。 */
    @Test public void releaseFailureRetainsCauseAndContextInBothLanguages() throws Exception {
        try (GlobalLockI18nFixture ignored = new GlobalLockI18nFixture()) {
            GlobalLockVo lock = new GlobalLockVo(123L, "generic", "config/resource", "{}");
            SQLException sql = new SQLException("Lock wait timeout", "HY000", 1205);
            RuntimeException cause = new RuntimeException("wrapper", sql);
            for (Locale locale : new Locale[]{Locale.CHINESE, Locale.ENGLISH}) {
                Locale.setDefault(locale);
                GlobalLockReleaseException failure = new GlobalLockReleaseException(123L, lock, "operation-1", "unlock", cause);
                assertSame(cause, failure.getCause());
                for (String value : new String[]{"lockId=123", "config/resource", "operation-1", "unlock", "SQLState=HY000", "errorCode=1205", "Lock wait timeout"}) {
                    assertTrue(failure.getMessage(), failure.getMessage().contains(value));
                }
                assertTrue(failure.getMessage().contains(locale == Locale.CHINESE ? "资源锁操作失败" : "Resource lock operation failed"));
            }
        }
    }

    /** 身份冲突应展示原值及请求值，同时避免输出完整的业务参数。 */
    @Test public void identityMismatchShowsOnlyRelevantFields() throws Exception {
        try (GlobalLockI18nFixture ignored = new GlobalLockI18nFixture()) {
            GlobalLockVo existing = new GlobalLockVo(123L, "generic", "config/old", "{\"token\":\"private-token\"}");
            GlobalLockVo request = new GlobalLockVo(123L, "generic", "config/new", "{}");
            String message = new GlobalLockIdentityMismatchException(existing, request, "execution", "old-instance", "new-instance").getMessage();
            for (String value : new String[]{"123", "config/old", "config/new", "execution", "old-instance", "new-instance"}) {
                assertTrue(message, message.contains(value));
            }
            assertFalse(message.contains("private-token"));
        }
    }
}
