package codedriver.framework.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.common.config.Config;
import codedriver.framework.exception.util.FreemarkerTransformException;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class FreemarkerUtil {
	static Logger logger = LoggerFactory.getLogger(FreemarkerUtil.class);

	public static String transform(Object paramObj, String content) throws FreemarkerTransformException {
		String resultStr = "";
		JSONObject dataObj = new JSONObject();
		dataObj.put("DATA", paramObj);
		String homeUrl = Config.HOME_URL();
		if(StringUtils.isNotBlank(homeUrl)) {
		    if(!homeUrl.endsWith(File.separator)) {
	            homeUrl += File.separator;
	        }
	        dataObj.put("homeUrl", homeUrl + TenantContext.get().getTenantUuid() + File.separator);
		}
        
		try {
			if (content != null) {
				Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
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
			}
		} catch (Exception ex) {
			throw new FreemarkerTransformException(ex.getMessage());
		}
		return resultStr;
	}

	public static void transform(Object paramObj, String content, Writer out) throws FreemarkerTransformException {
		// String resultStr = "";
		JSONObject dataObj = new JSONObject();
		dataObj.put("DATA", paramObj);
		String homeUrl = Config.HOME_URL();
		if(StringUtils.isNotBlank(homeUrl)) {
	        if(!homeUrl.endsWith(File.separator)) {
	            homeUrl += File.separator;
	        }
	        dataObj.put("homeUrl", homeUrl + TenantContext.get().getTenantUuid() + File.separator);
		}
		try {
			if (content != null && !content.equals("")) {
				Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
				cfg.setNumberFormat("0.##");
				cfg.setClassicCompatible(true);
				StringTemplateLoader stringLoader = new StringTemplateLoader();
				stringLoader.putTemplate("template", content);
				cfg.setTemplateLoader(stringLoader);
				Template temp;

				try {
					temp = cfg.getTemplate("template", "utf-8");
					temp.process(dataObj, out);
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					throw e;
				} catch (TemplateException e) {
					logger.error(e.getMessage(), e);
					throw e;
				}
			}
		} catch (Exception ex) {
			throw new FreemarkerTransformException(ex.getMessage());
		}
	}
}
