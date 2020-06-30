package codedriver.framework.util;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import codedriver.framework.exception.util.FreemarkerTransformException;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class JavascriptUtil {
	static Logger logger = LoggerFactory.getLogger(JavascriptUtil.class);
	private static StringBuilder scriptBuilder = new StringBuilder();
	private static final ScriptEngineManager sem = new ScriptEngineManager();
	private static final ScriptEngine se = sem.getEngineByName("nashorn");

	public static String transform(Object paramObj, String content) throws FreemarkerTransformException {
		se.put("DATA", paramObj);
		return "";
	}

}
