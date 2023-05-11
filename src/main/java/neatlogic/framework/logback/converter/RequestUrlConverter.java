/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.logback.converter;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import neatlogic.framework.asynchronization.threadlocal.RequestContext;

/**
 * logback.xml文件使用，日志中输出请求url
 * @author linbq
 * @since 2022/1/19 16:08
 **/
public class RequestUrlConverter extends ClassicConverter {
    /**
     * The convert method is responsible for extracting data from the event and
     * storing it for later use by the write method.
     *
     * @param event
     */
    @Override
    public String convert(ILoggingEvent event) {
        RequestContext requestContext = RequestContext.get();
        if (requestContext != null) {
            return requestContext.getUrl();
        }
        return "";
    }
}
