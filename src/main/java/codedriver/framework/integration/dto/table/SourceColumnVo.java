/*
 * Copyright(c) 2021 TechSureCo.,Ltd.AllRightsReserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.dto.table;

import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @author linbq
 * @since 2021/10/22 17:45
 **/
public class SourceColumnVo {
    private String column;
//    private Object value;
    private String expression;
    private List<String> valueList;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

//    public Object getValue() {
//        if (value == null && CollectionUtils.isNotEmpty(valueList)) {
//            value = valueList.get(0);
//        }
//        return value;
//    }
//
//    public void setValue(Object value) {
//        this.value = value;
//    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<String> getValueList() {
        return valueList;
    }

    public void setValueList(List<String> valueList) {
        this.valueList = valueList;
    }
}
