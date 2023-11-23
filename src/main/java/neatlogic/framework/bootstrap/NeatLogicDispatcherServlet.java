/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
