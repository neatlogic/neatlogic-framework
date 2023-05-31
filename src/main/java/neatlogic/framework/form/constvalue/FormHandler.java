/*
Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.

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

public enum FormHandler implements IFormHandler {
    FORMLABEL("formlabel", new I18n("common.tag")),
    FORMCASCADER("formcascader", new I18n("enum.framework.formhandler.formcascader")),
    FORMCHECKBOX("formcheckbox", new I18n("common.checkbox")),
    FORMDATE("formdate", new I18n("common.date")),
    FORMDIVIDER("formdivider", new I18n("enum.framework.formhandler.formdivider")),
    FORMTABLESELECTOR("formtableselector", new I18n("enum.framework.formhandler.formtableselector")),
    FORMCKEDITOR("formckeditor", new I18n("enum.framework.formhandler.formckeditor")),
    FORMLINK("formlink", new I18n("common.link")),
    FORMPRIORITY("formpriority", new I18n("enum.framework.formhandler.formpriority")),
    FORMRADIO("formradio", new I18n("common.radiobutton")),
    FORMSELECT("formselect", new I18n("common.select")),
    FORMTABLEINPUTER("formtableinputer", new I18n("enum.framework.formhandler.formtableinputer")),
    FORMTEXTAREA("formtextarea", new I18n("common.textarea")),
    FORMTEXT("formtext", new I18n("common.textbox")),
    FORMNUMBER("formnumber", new I18n("common.number")),
    FORMPASSWORD("formpassword", new I18n("common.password")),
    FORMTIME("formtime", new I18n("common.time")),
    FORMTREESELECT("formtreeselect", new I18n("enum.framework.formhandler.formtreeselect")),
    FORMUPLOAD("formupload", new I18n("enum.framework.formhandler.formupload")),
    FORMUSERSELECT("formuserselect", new I18n("common.userselector")),
    FORMCUBE("formcube", new I18n("enum.framework.formhandler.formcube")),
    FORMRATE("formrate", new I18n("common.score")),
    ;

    private final String handler;
    private final I18n handlerName;

    FormHandler(String handler, I18n handlerName) {
        this.handler = handler;
        this.handlerName = handlerName;
    }

    @Override
    public String getHandler() {
        return handler;
    }

    @Override
    public String getHandlerName() {
        return I18nUtils.getMessage(handlerName.toString());
    }
}
