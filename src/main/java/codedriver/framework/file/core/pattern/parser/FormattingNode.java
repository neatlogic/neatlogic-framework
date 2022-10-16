/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.file.core.pattern.parser;

import codedriver.framework.file.core.pattern.FormatInfo;

public class FormattingNode extends Node {

    FormatInfo formatInfo;

    FormattingNode(int type) {
        super(type);
    }

    FormattingNode(int type, Object value) {
        super(type, value);
    }

    public FormatInfo getFormatInfo() {
        return formatInfo;
    }

    public void setFormatInfo(FormatInfo formatInfo) {
        this.formatInfo = formatInfo;
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        if (!(o instanceof FormattingNode)) {
            return false;
        }
        FormattingNode r = (FormattingNode) o;

        return (formatInfo != null ? formatInfo.equals(r.formatInfo) : r.formatInfo == null);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (formatInfo != null ? formatInfo.hashCode() : 0);
        return result;
    }
}
