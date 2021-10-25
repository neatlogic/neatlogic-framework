/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.authentication.enums;

import codedriver.framework.common.constvalue.IEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;


public enum BodyType implements IEnum {
	RAW("raw"), X_WWW_FORM_URLENCODED("x-www-form-urlencoded");

	private String type;

	private BodyType(String _type) {
		this.type = _type;
	}

	public String toString() {
		return this.type;
	}


	@Override
	public List getValueTextList() {
		JSONArray array = new JSONArray();
		for(BodyType type : BodyType.values()){
			array.add(new JSONObject(){
				{
					this.put("value",type.toString());
					this.put("text",type.toString());
				}
			});
		}
		return array;
	}
}
