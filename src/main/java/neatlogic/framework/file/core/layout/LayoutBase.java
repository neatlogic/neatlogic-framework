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

package neatlogic.framework.file.core.layout;

import neatlogic.framework.util.TimeUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

abstract public class LayoutBase<E> implements Layout<E> {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(TimeUtil.YYYY_MM_DD_HH_MM_SS_SSS);

    protected boolean started;

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    public String getContentType() {
        return "text/plain";
    }

    @Override
    public String getFileHeader() {
        return "fileHeader##########" + LocalDateTime.now().format(dateTimeFormatter) + "##########fileHeader";
    }

    @Override
    public String getFileFooter() {
        return "fileFooter##########" + LocalDateTime.now().format(dateTimeFormatter) + "##########fileFooter";
    }
}
