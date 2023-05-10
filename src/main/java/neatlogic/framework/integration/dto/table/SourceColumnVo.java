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

package neatlogic.framework.integration.dto.table;

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
