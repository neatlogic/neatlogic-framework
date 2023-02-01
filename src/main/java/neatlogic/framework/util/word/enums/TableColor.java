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
package neatlogic.framework.util.word.enums;

/**
 * @author longrf
 * @date 2022/9/26 17:47
 */

public enum TableColor {
    BLACK("000000", "黑色"),
    WHITE("ffffff", "白色"),
    RED("FF0000", "红色"),
    BLUE("0000FF", "蓝色"),
    GREY("808080", "灰色"),
    ;

    private final String value;
    private final String text;

    private TableColor(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
