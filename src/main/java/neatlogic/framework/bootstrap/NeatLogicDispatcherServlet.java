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

package neatlogic.framework.bootstrap;

import neatlogic.framework.asynchronization.thread.ModuleInitApplicationListener;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.util.I18nUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class NeatLogicDispatcherServlet extends DispatcherServlet {
    public NeatLogicDispatcherServlet(ModuleVo moduleVo, WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
        this.moduleVo = moduleVo;
    }

    private final ModuleVo moduleVo;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            super.init(config);
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
            System.out.println("  ✖" + moduleVo.getId() + I18nUtils.getStaticMessage("nfb.neatlogicdispatcherservlet.init.error") + ": " + t.getMessage());
            //如果模块加载异常则删除模块信息
            ModuleUtil.removeModule(moduleVo);
        } finally {
            //当自定义模块加载失败时避免异步线程等待
            ModuleInitApplicationListener.getModuleinitphaser().arrive();
        }
    }
}
