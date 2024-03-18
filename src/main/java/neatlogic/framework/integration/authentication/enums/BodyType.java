/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

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

package neatlogic.framework.integration.authentication.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;

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
