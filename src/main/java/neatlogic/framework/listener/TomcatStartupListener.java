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

package neatlogic.framework.listener;

import neatlogic.framework.util.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Date;

@WebListener
public class TomcatStartupListener implements ServletContextListener {

    private final static Logger logger = LoggerFactory.getLogger(TomcatStartupListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 服务器启动时在out日志文件输出一行记录
        String message = String.format("[%s] 应用服务正在启动...", TimeUtil.convertDateToString(new Date(), "yyyy-MM-dd HH:mm:ss"));
        System.out.println(message);
    }
}
