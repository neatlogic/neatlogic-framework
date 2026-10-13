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

package neatlogic.framework.dto.healthcheck;

/**
 * 调用栈帧，保留结构化字段及 JVM 原始文本。
 */
public class ThreadSnapshotFrameVo {
    /** 类名称。 */
    private String className;
    /** 方法名称。 */
    private String methodName;
    /** 源码文件名称。 */
    private String fileName;
    /** 栈帧原始文本。 */
    private String text;
    /** 源码行号。 */
    private int lineNumber;
    /** 是否为本地方法。 */
    private boolean nativeMethod;

    /** 获取类名称。 */
    public String getClassName() {
        return className;
    }

    /** 设置类名称。 */
    public void setClassName(String className) {
        this.className = className;
    }

    /** 获取方法名称。 */
    public String getMethodName() {
        return methodName;
    }

    /** 设置方法名称。 */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /** 获取源码文件名称。 */
    public String getFileName() {
        return fileName;
    }

    /** 设置源码文件名称。 */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** 获取栈帧原始文本。 */
    public String getText() {
        return text;
    }

    /** 设置栈帧原始文本。 */
    public void setText(String text) {
        this.text = text;
    }

    /** 获取源码行号。 */
    public int getLineNumber() {
        return lineNumber;
    }

    /** 设置源码行号。 */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /** 获取是否为本地方法。 */
    public boolean isNativeMethod() {
        return nativeMethod;
    }

    /** 设置是否为本地方法。 */
    public void setNativeMethod(boolean nativeMethod) {
        this.nativeMethod = nativeMethod;
    }
}
