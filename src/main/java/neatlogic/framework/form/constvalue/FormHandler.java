/*Copyright (C) 2023  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

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
    FORMSUBASSEMBLY("formsubassembly", "子表单");

    private final String handler;
    private final String handlerName;

    FormHandler(String handler, String handlerName) {
        this.handler = handler;
        this.handlerName = handlerName;
    }

    public static String getName(String handler) {
        for (FormHandler formHandler : FormHandler.values()) {
            if (formHandler.handler.equals(handler)) {
                return formHandler.handlerName;
            }
        }
        return null;
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
