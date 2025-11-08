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

package neatlogic.framework.logback.boolex;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

/**
 * 优先使用白名单。及：如果白名单和黑名单同时配置，则使用白名单
 */
public class PathFilterEvaluator extends EventEvaluatorBase<ILoggingEvent> {

    private static AntPathMatcher antPathMatcher = new AntPathMatcher(".");

    private String whitelist;

    private String blacklist;

    @Override
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
        if(StringUtils.isNotBlank(whitelist)) {
            boolean flag = false;
            String[] whiteList = whitelist.split("\\|");
            String loggerName = event.getLoggerName();
            for (String pattern : whiteList) {
                if (antPathMatcher.match(pattern, loggerName)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return false;
            }
        }

        if(StringUtils.isNotBlank(blacklist)) {
            String[] blackList = blacklist.split("\\|");
            String loggerName = event.getLoggerName();
            for (String pattern : blackList) {
                if (antPathMatcher.match(pattern, loggerName)) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(String whitelist) {
        this.whitelist = whitelist;
    }

    public String getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(String blacklist) {
        this.blacklist = blacklist;
    }
}
