package codedriver.framework.common.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class RunScriptUtil {
	public static boolean runScript(String expression) throws ScriptException, NoSuchMethodException {
//		Pattern pattern = null;
//		Matcher matcher = null;
//		StringBuffer temp = new StringBuffer();
//		String regex = "\\$\\{([^}]+)\\}";
//		pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
//		matcher = pattern.matcher(expression);
//		List<String> stepAndKeyList = new ArrayList<String>();
//		while (matcher.find()) {
//			String key = matcher.group(1);
//			String[] keys = key.split("\\.");
//			String newkey = "";
//			for (String k : keys) {
//				newkey += "[\"" + k + "\"]";
//			}
//			matcher.appendReplacement(temp, "json" + newkey);
//			if (!stepAndKeyList.contains(matcher.group(1))) {
//				stepAndKeyList.add(matcher.group(1));
//			}
//		}
//		matcher.appendTail(temp);

		StringBuilder script = new StringBuilder();
		script.append("function run(){");
		script.append("return " + expression + ";\n");
		script.append("}");

//		JSONObject jsonObj = new JSONObject();
//		if (stepAndKeyList.size() > 0) {
//			for (String stepAndKey : stepAndKeyList) {
//				if (stepAndKey.indexOf(".") > -1 && stepAndKey.split("\\.").length == 2) {
//					String stepUid = stepAndKey.split("\\.")[0];
//					String key = stepAndKey.split("\\.")[1];
//					List<String> valueList = new ArrayList<>();// flowJobMapper.getFlowJobStepNodeParamValueByFlowJobIdUidKey(flowJobId,
//																// stepUid,
//																// key);
//					JSONObject valueObj = new JSONObject();
//					if (valueList.size() > 0) {
//						if (valueList.size() > 1) {
//							valueObj.put(key, valueList);
//						} else {
//							valueObj.put(key, valueList.get(0));
//						}
//					}
//					if (!valueObj.isEmpty()) {
//						if (!jsonObj.containsKey(stepUid)) {
//							jsonObj.put(stepUid, valueObj);
//						} else {
//							JSONObject tmpV = jsonObj.getJSONObject(stepUid);
//							// tmpV.accumulate(key, valueObj.get(key));
//							jsonObj.put(stepUid, tmpV);
//						}
//					}
//				}
//			}
//		}
		ScriptEngineManager sem = new ScriptEngineManager();
		ScriptEngine se = sem.getEngineByName("nashorn");
//		se.put("json", jsonObj);
		se.eval(script.toString());
		Invocable invocableEngine = (Invocable) se;
		Object callbackvalue = invocableEngine.invokeFunction("run");
		return Boolean.parseBoolean(callbackvalue.toString());
	}
	
	public static void main(String[] args) throws NoSuchMethodException, ScriptException {
		System.out.println(runScript("(true && true)"));
	}
}
