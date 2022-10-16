/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */
package codedriver.framework.file.core.pattern.parser;

public class CompositeNode extends SimpleKeywordNode {
    Node childNode;

    CompositeNode(String keyword) {
        super(Node.COMPOSITE_KEYWORD, keyword);

    }

    public Node getChildNode() {
        return childNode;
    }

    public void setChildNode(Node childNode) {
        this.childNode = childNode;
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof CompositeNode)) {
            return false;
        }
        CompositeNode r = (CompositeNode) o;

        return (childNode != null) ? childNode.equals(r.childNode) : (r.childNode == null);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (childNode != null) {
            buf.append("CompositeNode(" + childNode + ")");
        } else {
            buf.append("CompositeNode(no child)");
        }
        buf.append(printNext());
        return buf.toString();
    }
}