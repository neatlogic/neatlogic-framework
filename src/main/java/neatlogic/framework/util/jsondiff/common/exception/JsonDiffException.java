/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
