/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.integration.body.handler;

import neatlogic.framework.integration.authentication.enums.BodyType;
import neatlogic.framework.integration.body.core.IContentTypeHandler;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.net.HttpURLConnection;

public class UrlencodedContentTypeHandler implements IContentTypeHandler {

	@Override
	public String getType() {
		return BodyType.X_WWW_FORM_URLENCODED.toString();
	}

	@Override
	public String handleData(HttpURLConnection connection, JSONObject integrationParam, JSONObject config) {
		JSONArray paramList = config.getJSONArray("paramList");
		if (paramList != null && paramList.size() > 0) {
			String paramStr = "";
			for (int i = 0; i < paramList.size(); i++) {
				if (StringUtils.isBlank(paramStr)) {
					paramStr += "&";
				}
				JSONObject item = paramList.getJSONObject(i);
				String key = item.getString("key");
				String type = item.getString("type");
				String value = item.getString("value");
				if (type.equals("custom")) {
					paramStr += key + "=" + value;
				} else if (type.equals("mapping")) {
					paramStr += key + "=" + integrationParam.getString(value);
				}
			}
			return paramStr;
		}
		return "";
	}

}
