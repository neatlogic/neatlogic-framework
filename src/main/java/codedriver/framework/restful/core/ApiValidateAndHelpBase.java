/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.restful.core;

import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadlocal.UserContext;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.auth.core.AuthActionChecker;
import codedriver.framework.auth.core.AuthFactory;
import codedriver.framework.common.config.Config;
import codedriver.framework.common.constvalue.ApiParamType;
import codedriver.framework.common.util.IpUtil;
import codedriver.framework.dto.api.CacheControlVo;
import codedriver.framework.exception.resubmit.ResubmitException;
import codedriver.framework.exception.type.*;
import codedriver.framework.param.validate.core.ParamValidatorFactory;
import codedriver.framework.restful.annotation.*;
import codedriver.framework.restful.audit.ApiAuditSaveThread;
import codedriver.framework.restful.dto.ApiAuditVo;
import codedriver.framework.restful.dto.ApiVo;
import codedriver.framework.util.Md5Util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ApiValidateAndHelpBase {
    private static final Logger logger = LoggerFactory.getLogger(ApiValidateAndHelpBase.class);

    protected void saveAudit(ApiVo apiVo, JSONObject paramObj, Object result, String error, Long startTime, Long endTime) {
        ApiAuditVo audit = new ApiAuditVo();
        audit.setToken(apiVo.getToken());
        audit.setTenant(TenantContext.get().getTenantUuid());
        audit.setStatus(StringUtils.isNotBlank(error) ? ApiAuditVo.FAILED : ApiAuditVo.SUCCEED);
        audit.setServerId(Config.SCHEDULE_SERVER_ID);
        audit.setStartTime(new Date(startTime));
        audit.setEndTime(new Date(endTime));
        audit.setTimeCost(endTime - startTime);
        UserContext userContext = UserContext.get();
        audit.setUserUuid(userContext.getUserUuid());
        HttpServletRequest request = userContext.getRequest();
        String requestIp = IpUtil.getIpAddr(request);
        audit.setIp(requestIp);
        audit.setAuthtype(apiVo.getAuthtype());
        if (paramObj != null) {
            audit.setParam(paramObj.toJSONString());
        }
        if (error != null) {
            audit.setError(error);
        }
        if (result != null) {
            audit.setResult(JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
        }
        CachedThreadPool.execute(new ApiAuditSaveThread(audit));
    }

    private static void escapeXss(JSONObject paramObj, String key) {
        Object value = paramObj.get(key);
        if (value instanceof String) {
            try {
                JSONObject valObj = JSONObject.parseObject(value.toString());
                escapeXss(valObj);
                paramObj.replace(key, valObj.toJSONString());
            } catch (Exception ex) {
                try {
                    JSONArray valList = JSONArray.parseArray(value.toString());
                    encodeHtml(valList);
                    paramObj.replace(key, valList.toJSONString());
                } catch (Exception e) {
                    paramObj.replace(key, escapeXss(value.toString()));
                }
            }
        } else if (value instanceof JSONObject) {
            escapeXss((JSONObject) value);
            paramObj.replace(key, value);
        } else if (value instanceof JSONArray) {
            encodeHtml((JSONArray) value);
            paramObj.replace(key, value);
        }
    }

	/*
	private static Pattern scriptPattern = Pattern.compile("<script(.*?)</script>", Pattern.CASE_INSENSITIVE);
	private static Pattern javascriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
	private static Pattern evalPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOLL);
	*/

    private static String escapeXss(String str) {
        if (StringUtils.isNotBlank(str)) {
			/*
			 处理js xss注入
			 str = scriptPattern.matcher(str).replaceAll("");
			 str = javascriptPattern.matcher(str).replaceAll("");
			 str = evalPattern.matcher(str).replaceAll("");
			*/
            str = str.replace("&", "&amp;");
            str = str.replace("<", "&lt;");
            str = str.replace(">", "&gt;");
            str = str.replace("'", "&#39;");
            str = str.replace("\"", "&quot;");

            return str;
        }
        return "";
    }


    private static void escapeXss(JSONObject j) {
        Set<String> set = j.keySet();
        for (String s : set) {
            String newKey = escapeXss(s);
            if (!newKey.equals(s)) {
                Object value = j.get(s);
                j.remove(s);
                j.replace(newKey, value);
            }
            if (j.get(newKey) instanceof JSONObject) {
                escapeXss(j.getJSONObject(newKey));
            } else if (j.get(newKey) instanceof JSONArray) {
                encodeHtml(j.getJSONArray(newKey));
            } else if (j.get(newKey) instanceof String) {
                j.replace(newKey, escapeXss(j.getString(newKey)));
            }
        }
    }

    private static void encodeHtml(JSONArray j) {
        for (int i = 0; i < j.size(); i++) {
            if (j.get(i) instanceof JSONObject) {
                escapeXss(j.getJSONObject(i));
            } else if (j.get(i) instanceof JSONArray) {
                encodeHtml(j.getJSONArray(i));
            } else if (j.get(i) instanceof String) {
                j.set(i, escapeXss(j.getString(i)));
            }
        }
    }

    protected void validOutput(Class<?> apiClass, Object result, Class<?>... classes) throws NoSuchMethodException {
        Method method = apiClass.getMethod("myDoService", classes);
        Output output = method.getAnnotation(Output.class);
        if (output != null) {
            Param[] params = output.value();
            JSONObject outputObj = new JSONObject();
            if (params.length > 0) {
                for (Param p : params) {
                    if (!p.explode().getName().equals(NotDefined.class.getName())) {
                        if (p.explode().isArray()) {
                            outputObj.put(p.name(), "jsonArray");
                        } else {
                            for (Field field : p.explode().getDeclaredFields()) {
                                Annotation[] annotations = field.getDeclaredAnnotations();
                                if (annotations.length > 0) {
                                    for (Annotation annotation : annotations) {
                                        if (annotation.annotationType().equals(EntityField.class)) {
                                            EntityField entityField = (EntityField) annotation;
                                            String type;
                                            if (entityField.type().getValue().equalsIgnoreCase("integer") || entityField.type().getValue().equalsIgnoreCase("long")
                                                    || entityField.type().getValue().equalsIgnoreCase("int")) {
                                                type = "number";
                                            } else {
                                                type = entityField.type().getValue().toLowerCase();
                                            }
                                            outputObj.put(field.getName(), type);
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (p.type().getValue().equalsIgnoreCase("integer") || p.type().getValue().equalsIgnoreCase("long")
                                || p.type().getValue().equalsIgnoreCase("int")) {
                            outputObj.put(p.name(), "number");
                        } else {
                            outputObj.put(p.name(), p.type().getValue().toLowerCase());
                        }
                    }
                }
            }
            JSONObject returnObj = new JSONObject();
            returnObj.put("Return", result);
            JSONObject returnFormat = new JSONObject();
            if (returnObj.get("Return") instanceof JSONObject) {
                JSONObject obj = returnObj.getJSONObject("Return");
                for (String key : obj.keySet()) {
                    if (obj.get(key) instanceof String) {
                        returnFormat.put(key, "string");
                    } else if (obj.get(key) instanceof JSONObject) {
                        returnFormat.put(key, "jsonobject");
                    } else if (obj.get(key) instanceof JSONArray) {
                        returnFormat.put(key, "jsonarray");
                    } else if (obj.get(key) instanceof Number) {
                        returnFormat.put(key, "number");
                    } else if (obj.get(key) instanceof Boolean) {
                        returnFormat.put(key, "boolean");
                    }
                }
            }
            boolean isDifferent = false;
            if (MapUtils.isNotEmpty(returnFormat)) {
                for (String key : returnFormat.keySet()) {
                    if (!outputObj.containsKey(key)) {
                        isDifferent = true;
                    } else if (!returnFormat.getString(key).contains(outputObj.getString(key))) {
                        isDifferent = true;
                    }
                    if (isDifferent) {
                        break;
                    }
                }
            }
            if (isDifferent) {
                System.out.println("API:" + apiClass.getName());
                System.out.println("返回参数：" + returnFormat.toJSONString());
                System.out.println("接口配置：" + JSON.toJSONString(outputObj));
                System.out.println();
            }
        }
    }

    protected void validIsReSubmit(Class<?> targetClass, String token, JSONObject paramObj, Class<?>... classes) throws NoSuchMethodException {

        Method m = targetClass.getDeclaredMethod("myDoService", classes);
        if (m.isAnnotationPresent(ResubmitInterval.class)) {
            for (Annotation anno : m.getDeclaredAnnotations()) {
                if (anno.annotationType().equals(ResubmitInterval.class)) {
                    ResubmitInterval resubmitInterval = (ResubmitInterval) anno;
                    if (resubmitInterval.value() > 0) {
                        String key = token + "_" + Md5Util.encryptMD5(paramObj.toString());
                        if (SubmitKeyManager.contain(key)) {
                            throw new ResubmitException(token);
                        } else {
                            SubmitKeyManager.add(key, resubmitInterval.value());
                        }
                    }
                    break;
                }
            }
        }
    }

    protected void validApi(Class<?> apiClass, JSONObject paramObj, Class<?>... classes) throws NoSuchMethodException, SecurityException, PermissionDeniedException {
        // 获取目标类
        boolean isAuth = false;
        List<String> authNameList = new ArrayList<>();
        if (apiClass != null) {
            //AuthAction action = apiClass.getAnnotation(AuthAction.class);
            AuthAction[] actions = apiClass.getAnnotationsByType(AuthAction.class);
            if (actions.length > 0) {
                for (AuthAction action : actions) {
                    if (StringUtils.isNotBlank(action.action().getSimpleName())) {
                        String actionName = action.action().getSimpleName();
                        // 判断用户角色是否拥有接口权限
                        if (AuthActionChecker.check(actionName)) {
                            isAuth = true;
                            break;
                        }
                    }
                    authNameList.add(AuthFactory.getAuthInstance(action.action().getSimpleName()).getAuthDisplayName());
                }
            } else {
                isAuth = true;
            }

            if (!isAuth) {
                throw new PermissionDeniedException(authNameList);
            }
            // 判断参数是否合法
            Method method = apiClass.getMethod("myDoService", classes);
            validInput(method, paramObj);
        }
    }

    /**
     * @Description: 校验input注解的方法入参是否合法
     * @Author: 89770
     * @Date: 2021/2/20 13:04
     * @Params: [method, paramObj]
     * @Returns: void
     **/
    public void validInput(Method method, JSONObject paramObj) {
        Input input = method.getAnnotation(Input.class);
        if (input != null) {
            Param[] params = input.value();
            if (params.length > 0) {
                for (Param p : params) {
                    if (p.type().equals(ApiParamType.NOAUTH)) {
                        continue;
                    }
                    // xss过滤
                    if (p.xss() && paramObj.containsKey(p.name())) {
                        escapeXss(paramObj, p.name());
                    }

                    Object paramValue = null;

                    if (paramObj.containsKey(p.name())) {
                        // 参数类型校验
                        paramValue = paramObj.get(p.name());
                        // 如果值为null，则remove（前端约定不使用的接口参数会传null过来，故去掉）
                        if (paramValue == null) {
                            paramObj.remove(p.name());
                        }
                    }
                    // 前后去空格
                    if (p.type().equals(ApiParamType.STRING)) {
                        if (paramValue != null) {
                            paramObj.replace(p.name(), paramValue.toString().trim());
                        }
                    }
                    // 判断是否必填
                    if (p.isRequired()) {
                        if (!paramObj.containsKey(p.name())) {
                            throw new ParamNotExistsException(p.name());
                        } else {
                            if (p.type().equals(ApiParamType.STRING) && paramValue != null && StringUtils.isBlank(paramValue.toString())) {
                                throw new ParamNotExistsException(p.name());
                            }
                        }
                    }
                    //设置默认值
                    if (StringUtils.isNotBlank(p.defaultValue()) && !paramObj.containsKey(p.name())) {
                        try {
                            if (p.type().equals(ApiParamType.STRING)) {
                                paramObj.put(p.name(), p.defaultValue());
                            } else if (p.type().equals(ApiParamType.INTEGER)) {
                                paramObj.put(p.name(), Integer.parseInt(p.defaultValue()));
                            } else if (p.type().equals(ApiParamType.LONG)) {
                                paramObj.put(p.name(), Long.parseLong(p.defaultValue()));
                            } else if (p.type().equals(ApiParamType.JSONARRAY)) {
                                paramObj.put(p.name(), JSONArray.parse(p.defaultValue()));
                            } else if (p.type().equals(ApiParamType.JSONOBJECT)) {
                                paramObj.put(p.name(), JSONObject.parse(p.defaultValue()));
                            }
                        } catch (Exception ex) {
                            throw new ParamDefaultValueIrregularException(p.name(), p.defaultValue(), p.type());
                        }
                    }

                    // 判断最大长度
                    if (p.maxLength() > 0 && paramValue instanceof String) {
                        if (paramValue.toString().trim().length() > p.maxLength()) {
                            throw new ParamValueTooLongException(p.name(), paramValue.toString().length(), p.maxLength());
                        }
                    }
                    // 判断最小长度
                    if (p.minLength() > 0 && paramValue instanceof String) {
                        if (paramValue.toString().trim().length() < p.minLength()) {
                            throw new ParamValueTooShortException(p.name(), paramValue.toString().length(), p.minLength());
                        }
                    }
                    if (paramValue != null && !ParamValidatorFactory.getAuthInstance(p.type()).validate(paramValue, p.rule())) {
                        throw new ParamIrregularException(p.desc() + "（" + p.name() + "）");
                    }
                }
            }
        }
    }

    /*
     * @Description: 递归抽取字段信息
     * @Author: chenqiwei
     * @Date: 2021/1/8 2:14 下午
     * @Params: [field, paramList, loop]
     * @Returns: void
     **/
    private void drawFieldMessageRecursive(Field field, JSONArray paramList, boolean loop) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        if (annotations.length > 0) {
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(EntityField.class)) {
                    EntityField entityField = (EntityField) annotation;
                    JSONObject paramObj = new JSONObject();
                    paramObj.put("name", field.getName());
                    paramObj.put("type", entityField.type().getValue() + "[" + entityField.type().getText() + "]");
                    paramObj.put("description", entityField.name());

                    if (loop && field.getType().isAssignableFrom(List.class)) {
                        Type genericType = field.getGenericType();
                        if (genericType instanceof ParameterizedType) {
                            ParameterizedType parameterizedType = (ParameterizedType) genericType;
                            Type actualType = parameterizedType.getActualTypeArguments()[0];
                            if (actualType instanceof Class) { //如果不是class 则无需继续递归
                                Class<?> integerClass = (Class<?>) actualType;
                                JSONArray subParamList = new JSONArray();
                                for (Field subField : integerClass.getDeclaredFields()) {
                                    drawFieldMessageRecursive(subField, subParamList, false);
                                }
                                paramObj.put("children", subParamList);
                            }
                        }
                    }
                    paramList.add(paramObj);
                }
            }
        }
    }

    protected final JSONObject getApiComponentHelp(Class<?>... arg) {
        JSONObject jsonObj = new JSONObject();
        JSONArray inputList = new JSONArray();
        JSONArray outputList = new JSONArray();
        try {
            Method method = this.getClass().getDeclaredMethod("myDoService", arg);
            if (method.isAnnotationPresent(Input.class) || method.isAnnotationPresent(Output.class) || method.isAnnotationPresent(Description.class)) {
                for (Annotation anno : method.getDeclaredAnnotations()) {
                    if (anno.annotationType().equals(Input.class)) {
                        Input input = (Input) anno;
                        Param[] params = input.value();
                        if (params.length > 0) {
                            for (Param p : params) {

                                JSONObject paramObj = new JSONObject();
                                paramObj.put("name", p.name());
                                paramObj.put("type", p.type().getValue() + "[" + p.type().getText() + "]");
                                paramObj.put("isRequired", p.isRequired());
                                String description = p.desc();
                                if (StringUtils.isNotBlank(p.rule())) {
                                    description = description + "，合法输入：" + p.rule();
                                }
                                paramObj.put("description", description);
                                inputList.add(paramObj);
                                // }
                            }
                        }
                    } else if (anno.annotationType().equals(Output.class)) {
                        Output output = (Output) anno;
                        Param[] params = output.value();
                        if (params.length > 0) {
                            for (Param p : params) {
                                if (!p.explode().getName().equals(NotDefined.class.getName())) {
                                    //String paramNamePrefix = p.name();
                                    if (!p.explode().isArray()) {
                                        //paramNamePrefix = StringUtils.isBlank(paramNamePrefix) || "Return".equals(paramNamePrefix) ? "" : paramNamePrefix + ".";
                                        for (Field field : p.explode().getDeclaredFields()) {
                                            drawFieldMessageRecursive(field, outputList, true);
                                        }
                                    } else {
                                        JSONObject paramObj = new JSONObject();
                                        paramObj.put("name", p.name());
                                        paramObj.put("type", ApiParamType.JSONARRAY.getValue() + "[" + ApiParamType.JSONARRAY.getText() + "]");
                                        paramObj.put("description", p.desc());
                                        JSONArray elementObjList = new JSONArray();
                                        for (Field field : p.explode().getComponentType().getDeclaredFields()) {
                                            drawFieldMessageRecursive(field, elementObjList, true);
                                        }
                                        if (elementObjList.size() > 0) {
                                            paramObj.put("children", elementObjList);
                                        }

                                        outputList.add(paramObj);
                                    }
                                } else {
                                    JSONObject paramObj = new JSONObject();
                                    paramObj.put("name", p.name());
                                    paramObj.put("type", p.type().getValue() + "[" + p.type().getText() + "]");
                                    paramObj.put("description", p.desc());
                                    outputList.add(paramObj);
                                }
                            }
                        }
                    } else if (anno.annotationType().equals(Description.class)) {
                        Description description = (Description) anno;
                        jsonObj.put("description", description.desc());
                    } else if (anno.annotationType().equals(Example.class)) {
                        Example example = (Example) anno;
                        String content = example.example();
                        if (StringUtils.isNotBlank(content)) {
                            try {
                                content = JSONObject.parseObject(content).toString();
                            } catch (Exception ex) {
                                try {
                                    content = JSONArray.parseArray(content).toString();
                                } catch (Exception ignored) {

                                }
                            }
                            jsonObj.put("example", content);
                        }
                    }
                }
            }
        } catch (NoSuchMethodException | SecurityException e) {
            logger.error(e.getMessage());
        }
        if (!outputList.isEmpty()) {
            jsonObj.put("output", outputList);
        }
        if (!inputList.isEmpty()) {
            jsonObj.put("input", inputList);
        }
        return jsonObj;
    }

    private CacheControlVo cacheControlVo;

    public CacheControlVo getCacheControl(Class<?>... paramClass) {
        if (cacheControlVo == null) {
            CacheControl cacheControl = null;
            Annotation annotation = getAnnotation("myDoService", CacheControl.class, paramClass);
            if (annotation != null) {
                cacheControlVo = new CacheControlVo();
                cacheControl = (CacheControl) annotation;
                cacheControlVo.setCacheControlType(cacheControl.cacheControlType());
                cacheControlVo.setMaxAge(cacheControl.maxAge());
            }
        }
        return cacheControlVo;
    }

    /**
     * @Description: 根据注解，获取对应api的对应方法的注解
     * @Author: 89770
     * @Date: 2021/3/4 12:00
     * @Params: [t]
     * @Returns: java.lang.annotation.Annotation
     **/
    protected Annotation getAnnotation(String methodName, Class<?> t, Class<?>... paramClass) {
        Object target = null;
        try {
            Object proxy = AopContext.currentProxy();
            //获取代理的真实bean
            target = ((Advised) proxy).getTargetSource().getTarget();
        } catch (Exception ex) {
            target = this;
        }
        try {
            Method m = target.getClass().getDeclaredMethod(methodName, paramClass);
            if (m.isAnnotationPresent(CacheControl.class)) {
                for (Annotation anno : m.getDeclaredAnnotations()) {
                    if (anno.annotationType().equals(t)) {
                        return anno;
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            Throwable targetException = e;
            //如果是反射抛得异常，则需循环拆包，把真实得异常类找出来
            while (targetException instanceof InvocationTargetException) {
                targetException = ((InvocationTargetException) targetException).getTargetException();
            }
            targetException.printStackTrace();
        }
        return null;
    }
}
