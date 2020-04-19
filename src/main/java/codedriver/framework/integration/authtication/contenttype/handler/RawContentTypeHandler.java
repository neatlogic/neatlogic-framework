package codedriver.framework.integration.authtication.contenttype.handler;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.integration.authentication.costvalue.ContentType;
import codedriver.framework.integration.authtication.contenttype.core.IContentTypeHandler;

public class RawContentTypeHandler implements IContentTypeHandler {
	private static Pattern p = Pattern.compile("\\{\\{([^}]+)\\}\\}");

	@Override
	public String getType() {
		return ContentType.RAW.toString();
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
