/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.integration.body.handler;

import codedriver.framework.integration.authentication.enums.BodyType;
import codedriver.framework.integration.body.core.IContentTypeHandler;
import com.alibaba.fastjson.JSONObject;
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
