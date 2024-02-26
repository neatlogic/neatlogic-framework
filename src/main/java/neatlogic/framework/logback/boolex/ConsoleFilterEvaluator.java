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
