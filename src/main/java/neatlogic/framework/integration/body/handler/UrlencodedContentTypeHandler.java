/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.integration.body.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.integration.authentication.enums.BodyType;
import neatlogic.framework.integration.body.core.IContentTypeHandler;
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
