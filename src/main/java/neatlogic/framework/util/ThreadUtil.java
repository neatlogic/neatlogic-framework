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

package neatlogic.framework.util;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.common.config.Config;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ThreadUtil {

    public static void dumpTraces(Writer writer) throws IOException {
        ThreadMXBean mxBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = mxBean.getThreadInfo(mxBean.getAllThreadIds(), 0);
        Map<Long, ThreadInfo> threadInfoMap = new HashMap<>();
        for (ThreadInfo threadInfo : threadInfos) {
            threadInfoMap.put(threadInfo.getThreadId(), threadInfo);
        }
        Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
        long now = System.currentTimeMillis();
        String localAddr = StringUtils.EMPTY;
        String url = StringUtils.EMPTY;
        RequestContext requestContext = RequestContext.get();
        if (requestContext != null) {
            if (requestContext.getRequest() != null && StringUtils.isNotBlank(requestContext.getRequest().getLocalAddr())) {
                localAddr = requestContext.getRequest().getLocalAddr();
            }
            if (StringUtils.isNotBlank(requestContext.getUrl())) {
                url = requestContext.getUrl();
            }
        }
        writer.write("\n=================" + stacks.size() + " thread of " + localAddr + " at " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(now)) + " start.serverId is " + Config.SCHEDULE_SERVER_ID + "=================\n\n");
        for (Map.Entry<Thread, StackTraceElement[]> entry : stacks.entrySet()) {
            Thread thread = entry.getKey();
            writer.write("\"" + thread.getName() + "\" prio=" + thread.getPriority() + " tid=" + thread.getId() + " " + thread.getState() + " " + (thread.isDaemon() ? "deamon" : "worker"));
            ThreadInfo threadInfo = threadInfoMap.get(thread.getId());
            if (threadInfo != null) {
                writer.write(" native=" + threadInfo.isInNative() + ", suspended=" + threadInfo.isSuspended() + ", block=" + threadInfo.getBlockedCount() + ", wait=" + threadInfo.getWaitedCount());
                writer.write(" lock=" + threadInfo.getLockName() + " owned by " + threadInfo.getLockOwnerName() + " (" + threadInfo.getLockOwnerId() + "), cpu=" + (mxBean.getThreadCpuTime(threadInfo.getThreadId()) / 1000000L) + ", user=" + (mxBean.getThreadUserTime(threadInfo.getThreadId()) / 1000000L) + "\n");
            }
            for (StackTraceElement element : entry.getValue()) {
                writer.write("\t\t");
                writer.write(element.toString());
                writer.write("\n");
            }
            writer.write("\n");
        }
        writer.write("=================" + stacks.size() + " thread of " + url + " at " + new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z").format(new Date(now)) + " end.=================\n\n");
    }
}
