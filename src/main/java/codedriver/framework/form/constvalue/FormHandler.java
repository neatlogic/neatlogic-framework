/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.form.constvalue;

public enum FormHandler implements IFormHandler {
    FORMLABEL("formlabel", "标签"),
    FORMACCOUNTS("formaccounts", "账号组件"),
    FORMCASCADELIST("formcascadelist", "级联下拉"),
    FORMCHECKBOX("formcheckbox", "复选框"),
    FORMDATE("formdate", "日期"),
    FORMDIVIDER("formdivider", "分割线"),
    FORMDYNAMICLIST("formdynamiclist", "表格选择组件"),
    FORMTABLESELECTOR("formtableselector", "表格选择组件"),
    FORMEDITOR("formeditor", "富文本框"),
    FORMCKEDITOR("formckeditor", "富文本框"),
    FORMLINK("formlink", "链接"),
    FORMPRIORITY("formpriority", "修改优先级"),
    FORMRADIO("formradio", "单选框"),
    FORMSELECT("formselect", "下拉框"),
    FORMSTATICLIST("formstaticlist", "表格输入组件"),
    FORMTABLEINPUTER("formtableinputer", "表格输入组件"),
    FORMTEXTAREA("formtextarea", "文本域"),
    FORMINPUT("forminput", "文本框"),
    FORMTEXT("formtext", "文本框"),
    FORMNUMBER("formnumber", "数字"),
    FORMPASSWORD("formpassword", "密码"),
    FORMTIME("formtime", "时间"),
    FORMTREESELECT("formtreeselect", "下拉树组件"),
    FORMUPLOAD("formupload", "附件上传"),
    FORMUSERSELECT("formuserselect", "用户选择器"),
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
