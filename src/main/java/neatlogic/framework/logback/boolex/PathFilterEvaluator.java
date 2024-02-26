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
