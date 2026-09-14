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

/** 本轮通知的汇总结果，与锁释放结果相互独立。 */
public enum GlobalLockNotificationStatus {
    /** 已有等待者通知成功。 */
    SUCCESS("success", "globallock.status.notification.success"),
    /** 未通知成功且存在异常。 */
    FAILED("failed", "globallock.status.notification.failed"),
    /** 没有可通知的等待者。 */
    EMPTY("empty", "globallock.status.notification.empty");

    private final String value;
    private final String text;

    /** 保留既有接口及数据库状态值。 */
    GlobalLockNotificationStatus(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() { return value; }

    /** 按当前请求语言翻译，不能在枚举初始化时固定文案。 */
    public String getText() { return $.t(text); }

    /** 按协议值获取展示文案，未知状态交由调用方兼容处理。 */
    public static String getText(String value) {
        for (GlobalLockNotificationStatus status : values()) {
            if (status.getValue().equals(value)) return status.getText();
        }
        return "";
    }
}
