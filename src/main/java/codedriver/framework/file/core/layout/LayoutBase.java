/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core.layout;

import codedriver.framework.util.TimeUtil;

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
