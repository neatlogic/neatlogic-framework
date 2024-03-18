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
