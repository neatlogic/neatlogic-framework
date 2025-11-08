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

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.integration.authentication.enums.BodyType;
import neatlogic.framework.integration.body.core.IContentTypeHandler;
import org.apache.commons.lang3.StringUtils;

import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RawContentTypeHandler implements IContentTypeHandler {
	private static final Pattern p = Pattern.compile("\\{\\{([^}]+)}}");

	@Override
	public String getType() {
		return BodyType.RAW.toString();
	}

	@Override
	public String handleData(HttpURLConnection connection, JSONObject integrationParam, JSONObject config) {
		String body = config.getString("body");
		if (StringUtils.isNotBlank(body)) {
			Matcher matcher = p.matcher(body);
			StringBuffer temp = new StringBuffer();
			while (matcher.find()) {
				String val = integrationParam.getString(matcher.group(1));
				matcher.appendReplacement(temp, "\"" + val + "\"");
			}
			matcher.appendTail(temp);
			return temp.toString();
		}
		return "";
	}

}
