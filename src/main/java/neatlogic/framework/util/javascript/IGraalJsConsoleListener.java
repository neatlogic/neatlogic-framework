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

package neatlogic.framework.util.javascript;

/** 接收脚本控制台输出，不向监听器暴露 GraalVM 内部对象。 */
public interface IGraalJsConsoleListener {
    /** 接收 log、warn 或 error 级别的单条日志。 */
    void onConsole(String level, String message);

    /** 通知调用方日志已因条数或总长度限制被截断。 */
    default void onTruncated() {
    }
}
