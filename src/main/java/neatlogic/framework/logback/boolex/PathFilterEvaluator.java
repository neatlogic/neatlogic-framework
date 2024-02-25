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

/**
 * 优先使用白名单。及：如果白名单和黑名单同时配置，则使用白名单
 */
public class PathFilterEvaluator extends EventEvaluatorBase<ILoggingEvent> {

    private String whitelist;

    private String blacklist;

    @Override
    public boolean evaluate(ILoggingEvent event) throws NullPointerException, EvaluationException {
        if (StringUtils.isBlank(whitelist) && StringUtils.isBlank(blacklist)) {
            return true;
        }
        if(StringUtils.isNotBlank(whitelist)) {
            String[] whiteList = whitelist.split("\\|");
            String loggerName = event.getLoggerName();
            for (String white : whiteList) {
                if (loggerName.startsWith(white)) {
                    return true;
                }
            }
            return false;
        }

        if(StringUtils.isNotBlank(blacklist)) {
            String[] blackList = blacklist.split("\\|");
            String loggerName = event.getLoggerName();
            for (String black : blackList) {
                if (loggerName.startsWith(black)) {
                    return false;
                }
            }
            return true;
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
