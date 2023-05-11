package neatlogic.framework.util;

import com.alibaba.fastjson.JSONObject;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.util.FreemarkerTransformException;
import neatlogic.framework.notify.widget.FormTable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.io.Writer;

public class NotifyFreemarkerUtil {
	static Logger logger = LoggerFactory.getLogger(NotifyFreemarkerUtil.class);

	public static String transform(JSONObject paramObj, String content) throws FreemarkerTransformException {
		String resultStr = "";
		paramObj.put("formTable", new FormTable(paramObj));
		JSONObject dataObj = new JSONObject();
		dataObj.put("DATA", paramObj);
		String homeUrl = Config.HOME_URL();
		if(StringUtils.isNotBlank(homeUrl)) {
		    if(!homeUrl.endsWith("/")) {
	            homeUrl += "/";
	        }
	        dataObj.put("homeUrl", homeUrl + TenantContext.get().getTenantUuid() + "/");
		}
		try {
			if (content != null) {
				Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
				cfg.setNumberFormat("0.##");
				cfg.setClassicCompatible(true);
				StringTemplateLoader stringLoader = new StringTemplateLoader();
				stringLoader.putTemplate("template", content);
				cfg.setTemplateLoader(stringLoader);
				Template temp;
				Writer out = null;
				temp = cfg.getTemplate("template", "utf-8");
				out = new StringWriter();
				temp.process(dataObj, out);
				resultStr = out.toString();
				out.flush();
				out.close();
			}
		} catch (Exception ex) {
			logger.error("freeMarker Code：" + content);
			logger.error("JSON Code：" + dataObj.toJSONString());
			throw new FreemarkerTransformException(ex.getMessage());
		}
		return resultStr;
	}

//	public static void transform(Object paramObj, String content, Writer out) throws FreemarkerTransformException {
//		// String resultStr = "";
//		JSONObject dataObj = new JSONObject();
//		dataObj.put("DATA", paramObj);
//		String homeUrl = Config.HOME_URL();
//		if(StringUtils.isNotBlank(homeUrl)) {
//	        if(!homeUrl.endsWith("/")) {
//	            homeUrl += "/";
//	        }
//	        dataObj.put("homeUrl", homeUrl + TenantContext.get().getTenantUuid() + "/");
//		}
//		try {
//			if (content != null && !content.equals("")) {
//				Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
//				cfg.setNumberFormat("0.##");
//				cfg.setClassicCompatible(true);
//				StringTemplateLoader stringLoader = new StringTemplateLoader();
//				stringLoader.putTemplate("template", content);
//				cfg.setTemplateLoader(stringLoader);
//				Template temp;
//
//				try {
//					temp = cfg.getTemplate("template", "utf-8");
//					temp.process(dataObj, out);
//				} catch (IOException e) {
//					logger.error(e.getMessage(), e);
//					throw e;
//				} catch (TemplateException e) {
//					logger.error(e.getMessage(), e);
//					throw e;
//				}
//			}
//		} catch (Exception ex) {
//			throw new FreemarkerTransformException(ex.getMessage());
//		}
//	}
}
