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

public enum FormHandler implements IFormHandler {
    FORMLABEL("formlabel", "标签"),
    FORMCASCADER("formcascader", "级联下拉框"),
    FORMCHECKBOX("formcheckbox", "复选框"),
    FORMDATE("formdate", "日期"),
    FORMDIVIDER("formdivider", "分割线"),
    FORMTABLESELECTOR("formtableselector", "表格选择组件"),
    FORMCKEDITOR("formckeditor", "富文本框"),
    FORMLINK("formlink", "链接"),
    FORMPRIORITY("formpriority", "修改优先级"),
    FORMRADIO("formradio", "单选框"),
    FORMSELECT("formselect", "下拉框"),
    FORMTABLEINPUTER("formtableinputer", "表格输入组件"),
    FORMTEXTAREA("formtextarea", "文本域"),
    FORMTEXT("formtext", "文本框"),
    FORMNUMBER("formnumber", "数字"),
    FORMPASSWORD("formpassword", "密码"),
    FORMTIME("formtime", "时间"),
    FORMTREESELECT("formtreeselect", "下拉树组件"),
    FORMUPLOAD("formupload", "附件上传"),
    FORMUSERSELECT("formuserselect", "用户选择器"),
    FORMCUBE("formcube", "矩阵选择"),
    FORMRATE("formrate", "评分"),
    FORMTAB("formtab", "选项卡"),
    FORMCOLLAPSE("formcollapse", "折叠面板"),
    FORMSUBASSEMBLY("formsubassembly", "子表单"),
    ;

    private final String handler;
    private final String handlerName;

    FormHandler(String handler, String handlerName) {
        this.handler = handler;
        this.handlerName = handlerName;
    }

    @Override
    public String getHandler() {
        return handler;
    }

    @Override
    public String getHandlerName() {
        return handlerName;
    }
}
