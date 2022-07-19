/*
 * Copyright(c) 2022 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.bootstrap;

import codedriver.framework.asynchronization.thread.ModuleInitApplicationListener;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dto.module.ModuleVo;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class CodedriverDispatcherServlet extends DispatcherServlet {
    public CodedriverDispatcherServlet(ModuleVo moduleVo, WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
        this.moduleVo = moduleVo;
    }

    private final ModuleVo moduleVo;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            super.init(config);
        } catch (Throwable ignored) {
            //如果模块加载异常则删除模块信息
            ModuleUtil.removeModule(moduleVo);
        } finally {
            //当自定义模块加载失败时避免异步线程等待
            ModuleInitApplicationListener.getModuleinitphaser().arrive();
        }
    }
}
