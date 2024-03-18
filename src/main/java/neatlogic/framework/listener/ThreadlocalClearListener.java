/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.listener;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.cache.threadlocal.CacheContext;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

public class ThreadlocalClearListener implements ServletRequestListener {
    @Override
    public void requestDestroyed(ServletRequestEvent event) {
        // 清除所有threadlocal
        if (TenantContext.get() != null) {
            TenantContext.get().release();
        }
        if (UserContext.get() != null) {
            UserContext.get().release();
        }
        if (RequestContext.get() != null) {
            RequestContext.get().release();
        }
        CacheContext.release();
    }

    @Override
    public void requestInitialized(ServletRequestEvent event) {
        //必须实现方法 否则遇到低版本的javaee-api 会报错
    }
}
