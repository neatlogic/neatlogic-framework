/*
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package neatlogic.framework.logback.boolex;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/** 控制写入连接耗时日志的 HikariCP 事件，保留建连、关闭、补池和取连接超时的诊断信息。 */
public class HikariConnectionEventFilter extends Filter<ILoggingEvent> {

    /** 仅过滤 HikariCP 的低级别日志，自定义连接指标继续使用原有输出规则。 */
    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!event.getLoggerName().startsWith("com.zaxxer.hikari.pool.")
                || event.getLevel().isGreaterOrEqual(Level.WARN)) {
            return FilterReply.NEUTRAL;
        }

        // 匹配 HikariCP 4.0.3 的原始消息模板，避免格式化连接对象；异常事件保留完整堆栈。
        String message = event.getMessage();
        if ("{} - Added connection {}".equals(message)
                || "{} - Closing connection {}: {}".equals(message)
                || "{} - Cannot acquire connection from data source".equals(message)
                || "{} - Failed to create/setup connection: {}".equals(message)
                || "{} - Connection add failed, sleeping with backoff: {}ms".equals(message)) {
            return FilterReply.NEUTRAL;
        }
        // 补池、超时和例行巡检共用状态模板，第二个参数是带尾随空格的事件前缀。
        if ("{} - {}stats (total={}, active={}, idle={}, waiting={})".equals(message)) {
            Object[] args = event.getArgumentArray();
            if (args != null && args.length > 1
                    && ("After adding ".equals(args[1]) || "Timeout failure ".equals(args[1]))) {
                return FilterReply.NEUTRAL;
            }
        }
        return FilterReply.DENY;
    }
}
