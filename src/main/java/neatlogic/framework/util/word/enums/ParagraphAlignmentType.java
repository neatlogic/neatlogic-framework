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
package neatlogic.framework.util.word.enums;

/**
 * @author longrf
 * @date 2022/9/26 17:28
 */

public enum ParagraphAlignmentType {

    LEFT(1, "left"),
    CENTER(2, "center"),
    RIGHT(3, "right"),
    BOTH(4, "both"),
    ;

    private final Integer value;
    private final String text;

    private ParagraphAlignmentType(Integer value, String text) {
        this.value = value;
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
