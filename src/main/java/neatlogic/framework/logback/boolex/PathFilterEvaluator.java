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
