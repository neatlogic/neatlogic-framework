package codedriver.framework.attribute.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.attribute.constvalue.AttributeHandler;
import codedriver.framework.attribute.core.IAttributeHandler;
import codedriver.framework.attribute.dao.mapper.AttributeMapper;
import codedriver.framework.attribute.dto.AttributeDataVo;
import codedriver.framework.attribute.dto.AttributeVo;
import codedriver.framework.attribute.exception.AttributeValidException;

@Component
public class CustomHandler implements IAttributeHandler {

	@Autowired
	private AttributeMapper attributeMapper;

	@Override
	public String getType() {
		return AttributeHandler.CUSTOM.getValue();
	}

	@Override
	public boolean valid(AttributeDataVo attributeDataVo, JSONObject configObj) throws AttributeValidException {
		return true;
	}

	@Override
	public String getConfigPage() {
		return null;
	}

	@Override
	public String getInputPage() {
		return null;
	}

	@Override
	public String getViewPage() {
		return null;
	}

	@Override
	public Object getData(AttributeVo attributeVo, Map<String, String[]> paramMap) {
		return null;
	}

	@Override
	public List<String> getValueList(Object data) {
		List<String> valueList = new ArrayList<>();
		try {
			JSONObject jsonObj = JSONObject.parseObject(data.toString());
			if (jsonObj.containsKey("value")) {
				valueList.add(jsonObj.getString("value"));
			}
		} catch (Exception ex) {

		}
		return valueList;
	}

	@Override
	public String getText(Object data) {
		try {
			JSONObject jsonObj = JSONObject.parseObject(data.toString());
			if (jsonObj.containsKey("text")) {
				return jsonObj.getString("text");
			}
		} catch (Exception ex) {

		}
		return "";
	}

}
