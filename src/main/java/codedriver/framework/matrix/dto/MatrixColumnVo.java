package codedriver.framework.matrix.dto;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.collections4.CollectionUtils;

/**
 * @program: codedriver
 * @description:
 * @create: 2020-04-10 18:25
 **/
public class MatrixColumnVo {
    private String column;
    private List<String> valueList;
    private String expression;
    private String type;
	private List<String> defaultValue;
	@JSONField(serialize = false)
	private String value;
    public MatrixColumnVo() {
	}

	public MatrixColumnVo(String column, String value) {
		this.column = column;
		this.valueList = new ArrayList<>();
		this.valueList.add(value);
	}
	public MatrixColumnVo(String column, List<String> valueList) {
		this.column = column;
		this.valueList = valueList;
	}

	public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getValue() {
    	if(value == null && CollectionUtils.isNotEmpty(valueList)) {
    		value = valueList.get(0);
    	}
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

	public List<String> getValueList() {
		return valueList;
	}

	public void setValueList(List<String> valueList) {
		this.valueList = valueList;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(List<String> defaultValue) {
		this.defaultValue = defaultValue;
	}
}
