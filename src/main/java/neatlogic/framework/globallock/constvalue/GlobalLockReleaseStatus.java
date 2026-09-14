/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.globallock.constvalue;

import neatlogic.framework.util.$;

/** 目标锁的释放进度，只表达删除事务的执行结果。 */
public enum GlobalLockReleaseStatus {
    /** 等待释放。 */
    PENDING("pending", "globallock.status.release.pending"),
    /** 释放中。 */
    RELEASING("releasing", "globallock.status.release.releasing"),
    /** 已释放。 */
    RELEASED("released", "globallock.status.release.released"),
    /** 释放失败。 */
    FAILED("failed", "globallock.status.release.failed");

    private final String value;
    private final String text;

    /** 保留既有接口及数据库状态值。 */
    GlobalLockReleaseStatus(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() { return value; }

    /** 按当前请求语言翻译，不能在枚举初始化时固定文案。 */
    public String getText() { return $.t(text); }

    /** 按协议值获取展示文案，未知状态交由调用方兼容处理。 */
    public static String getText(String value) {
        for (GlobalLockReleaseStatus status : values()) {
            if (status.getValue().equals(value)) return status.getText();
        }
        return "";
    }
}
