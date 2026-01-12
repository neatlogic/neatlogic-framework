/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.bootstrap;

import neatlogic.framework.asynchronization.thread.ModuleInitApplicationListener;
import neatlogic.framework.changelog.ConnectionHolder;
import neatlogic.framework.common.util.ModuleUtil;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import neatlogic.framework.util.ChangelogUtil;
import neatlogic.framework.util.I18nUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModuleInitializer implements WebApplicationInitializer {
    static Logger logger = LoggerFactory.getLogger(ModuleInitializer.class);

    @Override
    public void onStartup(ServletContext context) throws ServletException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        //System.out.println("    _   __              __   __                  _          _____    ____ \n" + "   / | / /___   ____ _ / /_ / /   ____   ____ _ (_)_____   |__  /   / __ \\\n" + "  /  |/ // _ \\ / __ `// __// /   / __ \\ / __ `// // ___/    /_ <   / / / /\n" + " / /|  //  __// /_/ // /_ / /___/ /_/ // /_/ // // /__    ___/ /_ / /_/ / \n" + "/_/ |_/ \\___/ \\__,_/ \\__//_____/\\____/ \\__, //_/ \\___/   /____/(_)\\____/  \n" + "                                      /____/                             \n" + "===========================================================================");
        /*System.out.println("░▒▓███████▓▒░░▒▓████████▓▒░░▒▓██████▓▒░▒▓████████▓▒░▒▓█▓▒░      ░▒▓██████▓▒░ ░▒▓██████▓▒░░▒▓█▓▒░░▒▓██████▓▒░▒▓███████▓▒░       ░▒▓████████▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ ░▒▓█▓▒░   ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░     ░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ ░▒▓█▓▒░   ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░▒▓█▓▒░            ░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓██████▓▒░ ░▒▓████████▓▒░ ░▒▓█▓▒░   ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒▒▓███▓▒░▒▓█▓▒░▒▓█▓▒░     ░▒▓███████▓▒░       ░▒▓█▓▒░░▒▓█▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ ░▒▓█▓▒░   ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░▒▓█▓▒░            ░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░      ░▒▓█▓▒░░▒▓█▓▒░ ░▒▓█▓▒░   ░▒▓█▓▒░     ░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░▒▓█▓▒░▒▓█▓▒░░▒▓█▓▒░     ░▒▓█▓▒░▒▓██▓▒░▒▓█▓▒░░▒▓█▓▒░ \n" +
                "░▒▓█▓▒░░▒▓█▓▒░▒▓████████▓▒░▒▓█▓▒░░▒▓█▓▒░ ░▒▓█▓▒░   ░▒▓████████▓▒░▒▓██████▓▒░ ░▒▓██████▓▒░░▒▓█▓▒░░▒▓██████▓▒░▒▓███████▓▒░░▒▓██▓▒░▒▓████████▓▒░ \n" +
                "                                                                                                                                              ");*/
        System.out.println("""
               \s
                ███╗   ██╗███████╗ █████╗ ████████╗██╗      ██████╗  ██████╗ ██╗ ██████╗    ██╗  ██╗   ██╗  ██╗
                ████╗  ██║██╔════╝██╔══██╗╚══██╔══╝██║     ██╔═══██╗██╔════╝ ██║██╔════╝    ██║  ██║   ╚██╗██╔╝
                ██╔██╗ ██║█████╗  ███████║   ██║   ██║     ██║   ██║██║  ███╗██║██║         ███████║    ╚███╔╝
                ██║╚██╗██║██╔══╝  ██╔══██║   ██║   ██║     ██║   ██║██║   ██║██║██║         ╚════██║    ██╔██╗
                ██║ ╚████║███████╗██║  ██║   ██║   ███████╗╚██████╔╝╚██████╔╝██║╚██████╗         ██║██╗██╔╝ ██╗
                ╚═╝  ╚═══╝╚══════╝╚═╝  ╚═╝   ╚═╝   ╚══════╝ ╚═════╝  ╚═════╝ ╚═╝ ╚═════╝         ╚═╝╚═╝╚═╝  ╚═╝
                                                                                                             \s""");
        //生成地址:http://patorjk.com/software/taag/#p=display&v=1&f=ANSI%20Shadow&t=neatlogic%203.0
        ModuleVo module = null;
        try (ConnectionHolder neatlogicConnectionHolder = new ConnectionHolder()) {
            List<ModuleVo> moduleListFromServletContext = ChangelogUtil.getModuleListByServletContext(resolver);
            List<TenantVo> activeTenantList = ChangelogUtil.getAllTenantList(neatlogicConnectionHolder);
            ChangelogUtil.updateChangeLogVersion(resolver, activeTenantList, moduleListFromServletContext, neatlogicConnectionHolder);
            ChangelogUtil.initDmlSql(resolver, activeTenantList, moduleListFromServletContext, neatlogicConnectionHolder);
            System.out.println("⚡" + I18nUtils.getStaticMessage("common.startloadmodule"));
            List<ModuleVo> parentModuleList = moduleListFromServletContext.stream().filter(d -> d.getParent() == null).collect(Collectors.toList());
            List<ModuleVo> childModuleList = moduleListFromServletContext.stream().filter(d -> d.getParent() != null).collect(Collectors.toList());
            Set<String> successLoadedParentSet = new HashSet<>();
            for (ModuleVo moduleFromServletContext : parentModuleList) {
                module = moduleFromServletContext;
                NeatLogicWebApplicationContext appContext = new NeatLogicWebApplicationContext();
                appContext.setConfigLocation("classpath*:" + module.getPath());
                appContext.setId(module.getId());
                appContext.setModuleId(module.getId());
                appContext.setModuleName(module.getNameWithoutTranslate());
                appContext.setGroupName(module.getGroupNameWithoutTranslate());
                appContext.setGroup(module.getGroup());

                ModuleUtil.addModule(module);
                ServletRegistration.Dynamic sr = context.addServlet(module.getId() + "[" + I18nUtils.getStaticMessage(module.getNameWithoutTranslate()) + "] " + module.getVersion(), new NeatLogicDispatcherServlet(module, appContext));
                if (StringUtils.isNotBlank(module.getUrlMapping())) {
                    sr.addMapping(module.getUrlMapping());
                }
                /* 模块加载开始，计数器加一 **/
                ModuleInitApplicationListener.getModuleinitphaser().register();
                if (module.getId().equalsIgnoreCase("framework")) {
                    sr.addMapping("/");
                    sr.setLoadOnStartup(1);
                } else {
                    sr.addMapping("/__internal__" + module.getId() + "/*");//增加虚拟mapping，避免加载失败
                    sr.setLoadOnStartup(2);
                }
                System.out.println("  ✓" + module.getId() + "·" + I18nUtils.getStaticMessage(module.getNameWithoutTranslate()));
                successLoadedParentSet.add(module.getId());
                module = null;
            }
            for (ModuleVo childModule : childModuleList) {
                if (successLoadedParentSet.contains(childModule.getParent())) {
                    ModuleUtil.addModule(childModule);
                    if (!childModule.isCommercial()) {
                        //商业模块需要通过license校验后自行打印成功加载信息
                        System.out.println("  ✓" + childModule.getId() + "·" + I18nUtils.getStaticMessage(childModule.getNameWithoutTranslate()));
                    }
                }
            }
        } catch (ModuleInitRuntimeException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        } catch (Exception ex) {
            if (ModuleInitApplicationListener.getModuleinitphaser().getRegisteredParties() > 0) {
                ModuleInitApplicationListener.getModuleinitphaser().arriveAndDeregister();
            }
            logger.error(ex.getMessage(), ex);
        } finally {
            if (module != null) {
                System.out.println("  ✖" + module.getId() + "·" + I18nUtils.getStaticMessage(module.getNameWithoutTranslate()));
            }
        }
    }
}
