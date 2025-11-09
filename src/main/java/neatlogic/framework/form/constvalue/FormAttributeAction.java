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

package neatlogic.framework.form.constvalue;

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

public enum FormAttributeAction {
    HIDE("hide", new I18n("隐藏")),
    READ("read", new I18n("只读")),
    EDIT("edit", new I18n("编辑"));
    private String value;
    private I18n text;

    public String getValue() {
        return value;
    }

    public String getText() {
        return $.t(text.toString());
    }

    private FormAttributeAction(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

}
