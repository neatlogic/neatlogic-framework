/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.file.core.pattern.parser;

import java.util.List;

public class SimpleKeywordNode extends FormattingNode {

    List<String> optionList;

    SimpleKeywordNode(Object value) {
        super(Node.SIMPLE_KEYWORD, value);
    }

    protected SimpleKeywordNode(int type, Object value) {
        super(type, value);
    }

    public List<String> getOptions() {
        return optionList;
    }

    public void setOptions(List<String> optionList) {
        this.optionList = optionList;
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }

        if (!(o instanceof SimpleKeywordNode)) {
            return false;
        }
        SimpleKeywordNode r = (SimpleKeywordNode) o;

        return (optionList != null ? optionList.equals(r.optionList) : r.optionList == null);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (optionList == null) {
            buf.append("KeyWord(" + value + "," + formatInfo + ")");
        } else {
            buf.append("KeyWord(" + value + ", " + formatInfo + "," + optionList + ")");
        }
        buf.append(printNext());
        return buf.toString();
    }
}
