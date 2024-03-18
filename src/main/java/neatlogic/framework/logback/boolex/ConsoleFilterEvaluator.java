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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.boolex.EventEvaluatorBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

public class ConsoleFilterEvaluator extends EventEvaluatorBase<ILoggingEvent> {

    private static AntPathMatcher antPathMatcher = new AntPathMatcher(".");

    private String errorWhitelist;

    private String errorBlacklist;

    private String warnWhitelist;

    private String warnBlacklist;

    private String infoWhitelist;

    private String infoBlacklist;

    private String debugWhitelist;

    private String debugBlacklist;

    private String traceWhitelist;

    private String traceBlacklist;

    @Override
    public boolean evaluate(ILoggingEvent event) throws NullPointerException {
        if (event.getLevel() == Level.ERROR) {
            if(StringUtils.isNotBlank(errorWhitelist)) {
                boolean flag = false;
                String[] split = errorWhitelist.split("\\|");
                String loggerName = event.getLoggerName();
                for (String pattern : split) {
                    if (antPathMatcher.match(pattern, loggerName)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return false;
                }
            }
            if(StringUtils.isNotBlank(errorBlacklist)) {
                String[] split = errorBlacklist.split("\\|");
                String loggerName = event.getLoggerName();
                for (String pattern : split) {
                    if (antPathMatcher.match(pattern, loggerName)) {
                        return false;
                    }
                }
            }
        } else if (event.getLevel() == Level.WARN) {
            if(StringUtils.isNotBlank(warnWhitelist)) {
                boolean flag = false;
                String[] split = warnWhitelist.split("\\|");
                String loggerName = event.getLoggerName();
                for (String pattern : split) {
                    if (antPathMatcher.match(pattern, loggerName)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return false;
                }
            }
            if(StringUtils.isNotBlank(warnBlacklist)) {
                String[] split = warnBlacklist.split("\\|");
                String loggerName = event.getLoggerName();
                for (String pattern : split) {
                    if (antPathMatcher.match(pattern, loggerName)) {
                        return false;
                    }
                }
            }
        } else if (event.getLevel() == Level.INFO) {
            if(StringUtils.isNotBlank(infoWhitelist)) {
                boolean flag = false;
                String[] split = infoWhitelist.split("\\|");
                String loggerName = event.getLoggerName();
                for (String pattern : split) {
                    if (antPathMatcher.match(pattern, loggerName)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return false;
                }
            }
            if(StringUtils.isNotBlank(infoBlacklist)) {
                String[] split = infoBlacklist.split("\\|");
                String loggerName = event.getLoggerName();
                for (String pattern : split) {
                    if (antPathMatcher.match(pattern, loggerName)) {
                        return false;
                    }
                }
            }
        } else if (event.getLevel() == Level.DEBUG) {
            if(StringUtils.isNotBlank(debugWhitelist)) {
                boolean flag = false;
                String[] split = debugWhitelist.split("\\|");
                String loggerName = event.getLoggerName();
                for (String pattern : split) {
                    if (antPathMatcher.match(pattern, loggerName)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return false;
                }
            }
            if(StringUtils.isNotBlank(debugBlacklist)) {
                String[] split = debugBlacklist.split("\\|");
                String loggerName = event.getLoggerName();
                for (String pattern : split) {
                    if (antPathMatcher.match(pattern, loggerName)) {
                        return false;
                    }
                }
            }
        } else if (event.getLevel() == Level.TRACE) {
            if(StringUtils.isNotBlank(traceWhitelist)) {
                boolean flag = false;
                String[] split = traceWhitelist.split("\\|");
                String loggerName = event.getLoggerName();
                for (String pattern : split) {
                    if (antPathMatcher.match(pattern, loggerName)) {
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    return false;
                }
            }
            if(StringUtils.isNotBlank(traceBlacklist)) {
                String[] split = traceBlacklist.split("\\|");
                String loggerName = event.getLoggerName();
                for (String pattern : split) {
                    if (antPathMatcher.match(pattern, loggerName)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public String getErrorWhitelist() {
        return errorWhitelist;
    }

    public void setErrorWhitelist(String errorWhitelist) {
        this.errorWhitelist = errorWhitelist;
    }

    public String getErrorBlacklist() {
        return errorBlacklist;
    }

    public void setErrorBlacklist(String errorBlacklist) {
        this.errorBlacklist = errorBlacklist;
    }

    public String getWarnWhitelist() {
        return warnWhitelist;
    }

    public void setWarnWhitelist(String warnWhitelist) {
        this.warnWhitelist = warnWhitelist;
    }

    public String getWarnBlacklist() {
        return warnBlacklist;
    }

    public void setWarnBlacklist(String warnBlacklist) {
        this.warnBlacklist = warnBlacklist;
    }

    public String getInfoWhitelist() {
        return infoWhitelist;
    }

    public void setInfoWhitelist(String infoWhitelist) {
        this.infoWhitelist = infoWhitelist;
    }

    public String getInfoBlacklist() {
        return infoBlacklist;
    }

    public void setInfoBlacklist(String infoBlacklist) {
        this.infoBlacklist = infoBlacklist;
    }

    public String getDebugWhitelist() {
        return debugWhitelist;
    }

    public void setDebugWhitelist(String debugWhitelist) {
        this.debugWhitelist = debugWhitelist;
    }

    public String getDebugBlacklist() {
        return debugBlacklist;
    }

    public void setDebugBlacklist(String debugBlacklist) {
        this.debugBlacklist = debugBlacklist;
    }

    public String getTraceWhitelist() {
        return traceWhitelist;
    }

    public void setTraceWhitelist(String traceWhitelist) {
        this.traceWhitelist = traceWhitelist;
    }

    public String getTraceBlacklist() {
        return traceBlacklist;
    }

    public void setTraceBlacklist(String traceBlacklist) {
        this.traceBlacklist = traceBlacklist;
    }
}
