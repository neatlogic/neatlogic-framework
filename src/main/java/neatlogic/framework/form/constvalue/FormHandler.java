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

public enum FormHandler implements IFormHandler {
    FORMLABEL("formlabel", new I18n("enum.framework.formhandler.formlabel")),
    FORMCASCADER("formcascader", new I18n("enum.framework.formhandler.formcascader")),
    FORMCHECKBOX("formcheckbox", new I18n("enum.framework.formhandler.formcheckbox")),
    FORMDATE("formdate", new I18n("enum.framework.formhandler.formdate")),
    FORMDIVIDER("formdivider", new I18n("enum.framework.formhandler.formdivider")),
    FORMTABLESELECTOR("formtableselector", new I18n("enum.framework.formhandler.formtableselector")),
    FORMCKEDITOR("formckeditor", new I18n("enum.framework.formhandler.formckeditor")),
    FORMLINK("formlink", new I18n("enum.framework.formhandler.formlink")),
    FORMPRIORITY("formpriority", new I18n("enum.framework.formhandler.formpriority")),
    FORMRADIO("formradio", new I18n("enum.framework.formhandler.formradio")),
    FORMSELECT("formselect", new I18n("enum.framework.formhandler.formselect")),
    FORMTABLEINPUTER("formtableinputer", new I18n("enum.framework.formhandler.formtableinputer")),
    FORMTEXTAREA("formtextarea", new I18n("enum.framework.formhandler.formtextarea")),
    FORMTEXT("formtext", new I18n("enum.framework.formhandler.formtext")),
    FORMNUMBER("formnumber", new I18n("enum.framework.formhandler.formnumber")),
    FORMPASSWORD("formpassword", new I18n("enum.framework.formhandler.formpassword")),
    FORMTIME("formtime", new I18n("enum.framework.formhandler.formtime")),
    FORMTREESELECT("formtreeselect", new I18n("enum.framework.formhandler.formtreeselect")),
    FORMUPLOAD("formupload", new I18n("enum.framework.formhandler.formupload")),
    FORMUSERSELECT("formuserselect", new I18n("enum.framework.formhandler.formuserselect")),
    FORMCUBE("formcube", new I18n("enum.framework.formhandler.formcube")),
    FORMRATE("formrate", new I18n("enum.framework.formhandler.formrate")),
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
