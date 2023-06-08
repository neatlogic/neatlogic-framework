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

import neatlogic.framework.util.$;
import neatlogic.framework.util.I18n;

public enum FormHandler implements IFormHandler {
    FORMLABEL("formlabel", new I18n("标签")),
    FORMCASCADER("formcascader", new I18n("级联下拉框")),
    FORMCHECKBOX("formcheckbox", new I18n("复选框")),
    FORMDATE("formdate", new I18n("日期")),
    FORMDIVIDER("formdivider", new I18n("分割线")),
    FORMTABLESELECTOR("formtableselector", new I18n("表格选择组件")),
    FORMCKEDITOR("formckeditor", new I18n("富文本框")),
    FORMLINK("formlink", new I18n("链接")),
    FORMPRIORITY("formpriority", new I18n("修改优先级")),
    FORMRADIO("formradio", new I18n("单选框")),
    FORMSELECT("formselect", new I18n("下拉框")),
    FORMTABLEINPUTER("formtableinputer", new I18n("表格输入组件")),
    FORMTEXTAREA("formtextarea", new I18n("文本域")),
    FORMTEXT("formtext", new I18n("文本框")),
    FORMNUMBER("formnumber", new I18n("数字")),
    FORMPASSWORD("formpassword", new I18n("密码")),
    FORMTIME("formtime", new I18n("时间")),
    FORMTREESELECT("formtreeselect", new I18n("下拉树组件")),
    FORMUPLOAD("formupload", new I18n("附件上传")),
    FORMUSERSELECT("formuserselect", new I18n("用户选择器")),
    FORMCUBE("formcube", new I18n("矩阵选择")),
    FORMRATE("formrate", new I18n("评分")),
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
        return $.t(handlerName.toString());
    }
}
