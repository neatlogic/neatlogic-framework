/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

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
