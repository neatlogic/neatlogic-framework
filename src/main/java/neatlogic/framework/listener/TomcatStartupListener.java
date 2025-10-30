/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class TomcatStartupListener implements ServletContextListener {

    private final static Logger logger = LoggerFactory.getLogger(TomcatStartupListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 服务器启动时在日志文件输出一行记录
        String message = "应用服务正在启动...";
        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
        if (logger.isWarnEnabled()) {
            logger.warn(message);
        }
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
        if (logger.isTraceEnabled()) {
            logger.trace(message);
        }
    }
}
