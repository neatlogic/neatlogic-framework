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

package neatlogic.framework.util.jsondiff.common.exception;


public class JsonDiffException extends RuntimeException {

    //异常信息
    private final String message;

    //构造函数
    public JsonDiffException(String message) {
        super(message);
        this.message = message;
    }


    /**
     * 构造方法
     */
    public JsonDiffException(String message, Throwable e) {
        super(message, e);
        this.message = message;
    }


}
