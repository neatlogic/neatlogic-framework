package codedriver.framework.integration.authtication.contenttype.handler;

import java.net.HttpURLConnection;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.integration.authentication.costvalue.ContentType;
import codedriver.framework.integration.authtication.contenttype.core.IContentTypeHandler;

public class UrlencodedContentTypeHandler implements IContentTypeHandler {

	@Override
	public String getType() {
		return ContentType.X_WWW_FORM_URLENCODED.toString();
	}

	@Override
	public String handleData(HttpURLConnection connection, JSONObject integrationParam, JSONObject config) {
		JSONArray paramList = config.getJSONArray("paramList");
		if (paramList != null && paramList.size() > 0) {
			String paramstr = "";
			for (int i = 0; i < paramList.size(); i++) {
				if (StringUtils.isBlank(paramstr)) {
					paramstr += "&";
				}
				JSONObject item = paramList.getJSONObject(i);
				String key = item.getString("key");
				String type = item.getString("type");
				String value = item.getString("value");
				if (type.equals("custom")) {
					paramstr += key + "=" + value;
				} else if (type.equals("mapping")) {
					paramstr += key + "=" + integrationParam.getString(value);
				}
			}
			return paramstr;
		}
		return "";
	}

}
