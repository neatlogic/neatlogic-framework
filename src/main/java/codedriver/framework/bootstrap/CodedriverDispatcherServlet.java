/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.bootstrap;

import codedriver.framework.asynchronization.thread.ModuleInitApplicationListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class CodedriverDispatcherServlet extends DispatcherServlet {
    public CodedriverDispatcherServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            super.init(config);
        } finally {
            //当自定义模块加载失败时避免异步线程等待
            ModuleInitApplicationListener.getModuleinitphaser().arrive();
        }
    }
}
