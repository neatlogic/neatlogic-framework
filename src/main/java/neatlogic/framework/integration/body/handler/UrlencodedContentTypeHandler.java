/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
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
