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
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.exception.module.ModuleInitRuntimeException;
import neatlogic.framework.util.ChangelogUtil;
import neatlogic.framework.util.I18nUtils;
import neatlogic.framework.util.JdbcUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.sql.Connection;
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
        System.out.println("███╗   ██╗███████╗ █████╗ ████████╗██╗      ██████╗  ██████╗ ██╗ ██████╗    ██████╗     ██████╗ \n" +
                "████╗  ██║██╔════╝██╔══██╗╚══██╔══╝██║     ██╔═══██╗██╔════╝ ██║██╔════╝    ╚════██╗   ██╔═████╗\n" +
                "██╔██╗ ██║█████╗  ███████║   ██║   ██║     ██║   ██║██║  ███╗██║██║          █████╔╝   ██║██╔██║\n" +
                "██║╚██╗██║██╔══╝  ██╔══██║   ██║   ██║     ██║   ██║██║   ██║██║██║          ╚═══██╗   ████╔╝██║\n" +
                "██║ ╚████║███████╗██║  ██║   ██║   ███████╗╚██████╔╝╚██████╔╝██║╚██████╗    ██████╔╝██╗╚██████╔╝\n" +
                "╚═╝  ╚═══╝╚══════╝╚═╝  ╚═╝   ╚═╝   ╚══════╝ ╚═════╝  ╚═════╝ ╚═╝ ╚═════╝    ╚═════╝ ╚═╝ ╚═════╝ \n" +
                "                                                                                                ");
        //生成地址:http://patorjk.com/software/taag/#p=display&v=1&f=ANSI%20Shadow&t=neatlogic%203.0
        ModuleVo module = null;
        try (Connection neatlogicConn = JdbcUtil.getNeatlogicConnection()) {
            List<ModuleVo> moduleListFromServletContext = ChangelogUtil.getModuleListByServletContext(resolver);
            List<TenantVo> activeTenantList = ChangelogUtil.getAllTenantList(neatlogicConn);
            ChangelogUtil.updateChangeLogVersion(resolver, activeTenantList, moduleListFromServletContext, neatlogicConn);
            ChangelogUtil.initDmlSql(resolver, activeTenantList, moduleListFromServletContext, neatlogicConn);
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
