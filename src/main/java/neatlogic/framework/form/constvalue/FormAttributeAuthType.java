/*
Copyright(c) $today.year NeatLogic Co., Ltd. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package neatlogic.framework.form.constvalue;

import neatlogic.framework.util.I18n;
import neatlogic.framework.util.I18nUtils;

public enum FormAttributeAuthType {
    COMPONENT("component", new I18n("enum.framework.formattributeauthtype.component")),
    ROW("row", new I18n("enum.framework.formattributeauthtype.row"));
    private String value;
    private I18n text;

    public String getValue() {
        return value;
    }

    public String getText() {
        return I18nUtils.getMessage(text.toString());
    }

    private FormAttributeAuthType(String value, I18n text) {
        this.value = value;
        this.text = text;
    }

}
