package codedriver.framework.util;

import java.io.StringWriter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

public class JavascriptUtil {
	static Logger logger = LoggerFactory.getLogger(JavascriptUtil.class);
	private static final ScriptEngineManager sem = new ScriptEngineManager();

	public static String transform(Object paramObj, String script) throws ScriptException, NoSuchMethodException {
		return transform(paramObj, script, null);
	}

	public static String transform(Object paramObj, String script, StringWriter sw) throws ScriptException, NoSuchMethodException {
		ScriptEngine se = sem.getEngineByName("nashorn");
		if (sw != null) {
			se.getContext().setWriter(sw);
		}
		se.put("O", paramObj);
		if (StringUtils.isBlank(script)) {
			se.put("N", paramObj);
		}
		se.put("N", null);
		se.eval("try{O = JSON.parse(O);}catch(e){}");
		se.eval(script);
		Object result = se.eval("JSON.stringify(N);");
		String resultStr = "";
		if (result != null) {
			try {
				resultStr = result.toString();// JSON.toJSONString(result);
			} catch (Exception ex) {
				resultStr = result.toString();
			}
		}
		return resultStr;
	}

	public static void main(String[] argv) throws ScriptException, NoSuchMethodException {
		JSONObject paramObj = JSONObject.parseObject("{\n" + "  \"Return\": {\n" + "    \"viewId\": 40,\n" + "    \"columnList\": [\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 0,\n" + "        \"label\": \"名称\"\n" + "      },\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 235,\n" + "        \"label\": \"主机名称\"\n" + "      },\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 233,\n" + "        \"label\": \"系统IP\"\n" + "      },\n"
				+ "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 234,\n" + "        \"label\": \"浮动IP\"\n" + "      },\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 266,\n" + "        \"label\": \"序列号\"\n" + "      },\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 255,\n" + "        \"label\": \"所属环境\"\n" + "      },\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 351,\n" + "        \"label\": \"root密码\"\n"
				+ "      },\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 328,\n" + "        \"label\": \"运行状态\"\n" + "      },\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 327,\n" + "        \"label\": \"网络区域\"\n" + "      },\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 236,\n" + "        \"label\": \"系统类型\"\n" + "      },\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 237,\n"
				+ "        \"label\": \"CPU核数\"\n" + "      },\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 238,\n" + "        \"label\": \"内存\"\n" + "      },\n" + "      {\n" + "        \"ciId\": 83,\n" + "        \"attrId\": 239,\n" + "        \"label\": \"磁盘空间\"\n" + "      }\n" + "    ]\n" + "  }\n" + "}");
		// String script = "N = {}; for(var i = 0; i < O.userList.length; i++){\n" +
		// "var userObj = O.userList[i];\n" + " if(!N[userObj['title']]){\n" +
		// "N[userObj['title']] = new Array();\n" + " }\n" +
		// "N[userObj['title']].push(userObj['username']);\n" + " }";
		String script = "N = [];\n" + "for(var i = 0; i < O.Return.columnList.length; i++){\n" + " print(typeof O.Return.columnList[i]);N.push(O.Return.columnList[i]);  \n" + "}print(N);";
		String script2 = "N = [];var tmp = {};tmp['Return'] ={};tmp['Return']['columnList'] = [{\"attrId\":327,\"label\":\"网络区域\",\"ciId\":83}];  " + "for(var i = 0; i < tmp.Return.columnList.length; i++){N.push(tmp.Return.columnList[i])};print(N);";
		String script3 = "var nn= new Array();nn.push(O.Return.columnList[0]);print(JSON.stringify(O.Return.columnList[0]));";
		JSONObject paramObj2 = new JSONObject();
		paramObj2.put("a", "abc");
		String script4 = "print(typeof O);print(JSON.stringify(O));";
		System.out.println(transform("abc", script4));
	}

}
