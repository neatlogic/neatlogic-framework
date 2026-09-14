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

/** 交互操作的持久化生命周期，与释放和通知结果分别记录。 */
public enum GlobalLockOperationStatus {
    /** 已准备。 */
    PREPARED("prepared", "globallock.status.operation.prepared"),
    /** 执行中。 */
    RUNNING("running", "globallock.status.operation.running"),
    /** 操作结束，通知可能失败。 */
    DONE("done", "globallock.status.operation.done"),
    /** 操作失败。 */
    FAILED("failed", "globallock.status.operation.failed");

    private final String value;
    private final String text;

    /** 保留既有接口及数据库状态值。 */
    GlobalLockOperationStatus(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() { return value; }

    /** 按当前请求语言翻译，不能在枚举初始化时固定文案。 */
    public String getText() { return $.t(text); }

    /** 按协议值获取展示文案，未知状态交由调用方兼容处理。 */
    public static String getText(String value) {
        for (GlobalLockOperationStatus status : values()) {
            if (status.getValue().equals(value)) return status.getText();
        }
        return "";
    }
}
