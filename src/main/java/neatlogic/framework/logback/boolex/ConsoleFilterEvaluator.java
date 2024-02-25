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

public class ConsoleFilterEvaluator extends EventEvaluatorBase<ILoggingEvent> {

    private String errorPathList;

    private String warnPathList;

    private String infoPathList;

    private String debugPathList;

    private String tracePathList;

    @Override
    public boolean evaluate(ILoggingEvent event) throws NullPointerException {
        if (event.getLevel() == Level.ERROR) {
            if(StringUtils.isNotBlank(errorPathList)) {
                String[] split = errorPathList.split("\\|");
                String loggerName = event.getLoggerName();
                for (String path : split) {
                    if (loggerName.startsWith(path)) {
                        return true;
                    }
                }
                return false;
            }
        } else if (event.getLevel() == Level.WARN) {
            if(StringUtils.isNotBlank(warnPathList)) {
                String[] split = warnPathList.split("\\|");
                String loggerName = event.getLoggerName();
                for (String path : split) {
                    if (loggerName.startsWith(path)) {
                        return true;
                    }
                }
                return false;
            }
        } else if (event.getLevel() == Level.INFO) {
            if(StringUtils.isNotBlank(infoPathList)) {
                String[] split = infoPathList.split("\\|");
                String loggerName = event.getLoggerName();
                for (String path : split) {
                    if (loggerName.startsWith(path)) {
                        return true;
                    }
                }
                return false;
            }
        } else if (event.getLevel() == Level.DEBUG) {
            if(StringUtils.isNotBlank(debugPathList)) {
                String[] split = debugPathList.split("\\|");
                String loggerName = event.getLoggerName();
                for (String path : split) {
                    if (loggerName.startsWith(path)) {
                        return true;
                    }
                }
                return false;
            }
        } else if (event.getLevel() == Level.TRACE) {
            if(StringUtils.isNotBlank(tracePathList)) {
                String[] split = tracePathList.split("\\|");
                String loggerName = event.getLoggerName();
                for (String path : split) {
                    if (loggerName.startsWith(path)) {
                        return true;
                    }
                }
                return false;
            }
        }
        return true;
    }

    public String getErrorPathList() {
        return errorPathList;
    }

    public void setErrorPathList(String errorPathList) {
        this.errorPathList = errorPathList;
    }

    public String getWarnPathList() {
        return warnPathList;
    }

    public void setWarnPathList(String warnPathList) {
        this.warnPathList = warnPathList;
    }

    public String getInfoPathList() {
        return infoPathList;
    }

    public void setInfoPathList(String infoPathList) {
        this.infoPathList = infoPathList;
    }

    public String getDebugPathList() {
        return debugPathList;
    }

    public void setDebugPathList(String debugPathList) {
        this.debugPathList = debugPathList;
    }

    public String getTracePathList() {
        return tracePathList;
    }

    public void setTracePathList(String tracePathList) {
        this.tracePathList = tracePathList;
    }
}
