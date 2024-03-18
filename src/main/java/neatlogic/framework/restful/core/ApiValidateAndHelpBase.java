/*Copyright (C) 2024  深圳极向量科技有限公司 All Rights Reserved.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.*/

package neatlogic.framework.restful.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.auth.core.AuthActionChecker;
import neatlogic.framework.auth.core.AuthFactory;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.common.util.IpUtil;
import neatlogic.framework.crossover.CrossoverServiceFactory;
import neatlogic.framework.dto.api.CacheControlVo;
import neatlogic.framework.exception.resubmit.ResubmitException;
import neatlogic.framework.exception.type.*;
import neatlogic.framework.file.core.AuditType;
import neatlogic.framework.file.core.Event;
import neatlogic.framework.file.core.appender.AppenderManager;
import neatlogic.framework.param.validate.core.ParamValidatorFactory;
import neatlogic.framework.reflection.ReflectionManager;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.util.$;
import neatlogic.framework.util.Md5Util;
import neatlogic.framework.util.XssUtil;
import neatlogic.module.framework.restful.apiaudit.ApiAuditAppendPostProcessor;
import neatlogic.module.framework.restful.apiaudit.ApiAuditAppendPreProcessor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.framework.AopContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApiValidateAndHelpBase {
    private static final Logger logger = LoggerFactory.getLogger(ApiValidateAndHelpBase.class);

    protected void saveAudit(ApiVo apiVo, JSONObject paramObj, Object result, String error, Long startTime, Long endTime) {
        UserContext userContext = UserContext.get();
        HttpServletRequest request = userContext.getRequest();
        String requestIp = IpUtil.getIpAddr(request);
        JSONObject data = new JSONObject();
        data.put("token", apiVo.getToken());
        data.put("authtype", apiVo.getAuthtype());
        data.put("userUuid", userContext.getUserUuid());
        data.put("ip", requestIp);
        if (MapUtils.isNotEmpty(paramObj)) {
            data.put("param", JSONObject.toJSONString(paramObj, SerializerFeature.PrettyFormat, SerializerFeature.WriteDateUseDateFormat));
        }
        if (result != null) {
            data.put("result", JSONObject.toJSONString(result, SerializerFeature.PrettyFormat, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect));
        }
        if (StringUtils.isNotBlank(error)) {
            data.put("error", error);
        }
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        ApiAuditAppendPostProcessor appendPostProcessor = CrossoverServiceFactory.getApi(ApiAuditAppendPostProcessor.class);
        ApiAuditAppendPreProcessor appendPreProcessor = CrossoverServiceFactory.getApi(ApiAuditAppendPreProcessor.class);
        AppenderManager.execute(new Event(apiVo.getToken(), startTime, data, appendPreProcessor, appendPostProcessor, AuditType.API_AUDIT));
    }

    protected void saveAudit(ApiVo apiVo, String param, Object result, String error, Long startTime, Long endTime) {
        UserContext userContext = UserContext.get();
        HttpServletRequest request = userContext.getRequest();
        String requestIp = IpUtil.getIpAddr(request);
        JSONObject data = new JSONObject();
        data.put("token", apiVo.getToken());
        data.put("authtype", apiVo.getAuthtype());
        data.put("userUuid", userContext.getUserUuid());
        data.put("ip", requestIp);
        if (StringUtils.isNotEmpty(param)) {
            data.put("param", param);
        }
        if (result != null) {
            data.put("result", JSONObject.toJSONString(result, SerializerFeature.PrettyFormat, SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect));
        }
        if (StringUtils.isNotBlank(error)) {
            data.put("error", error);
        }
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        ApiAuditAppendPostProcessor appendPostProcessor = CrossoverServiceFactory.getApi(ApiAuditAppendPostProcessor.class);
        ApiAuditAppendPreProcessor appendPreProcessor = CrossoverServiceFactory.getApi(ApiAuditAppendPreProcessor.class);
        AppenderManager.execute(new Event(apiVo.getToken(), startTime, data, appendPreProcessor, appendPostProcessor, AuditType.API_AUDIT));
    }


    protected void validOutput(Class<?> apiClass, Object result, Class<?>... classes) throws NoSuchMethodException {
        Method method = apiClass.getMethod("myDoService", classes);
        Output output = method.getAnnotation(Output.class);
        if (output != null) {
            Param[] params = output.value();
            JSONObject outputObj = new JSONObject();
            for (Param p : params) {
                if (!p.explode().getName().equals(NotDefined.class.getName())) {
                    if (p.explode().isArray()) {
                        outputObj.put($.t(p.name()), "jsonArray");
                    } else {
                        for (Field field : p.explode().getDeclaredFields()) {
                            Annotation[] annotations = field.getDeclaredAnnotations();
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
                                    outputObj.put($.t(field.getName()), type);
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
                System.out.println("接口配置：" + JSONObject.toJSONString(outputObj));
                System.out.println();
            }
        }
    }

    protected void validIsReSubmitForRaw(Class<?> targetClass, String token, String param, Class<?>... classes) throws NoSuchMethodException {
        Method m = targetClass.getDeclaredMethod("myDoService", classes);
        if (m.isAnnotationPresent(ResubmitInterval.class)) {
            for (Annotation anno : m.getDeclaredAnnotations()) {
                if (anno.annotationType().equals(ResubmitInterval.class)) {
                    ResubmitInterval resubmitInterval = (ResubmitInterval) anno;
                    if (resubmitInterval.value() > 0) {
                        String key = token + "_" + Md5Util.encryptMD5(param);
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

    protected void validApiFowRaw(Class<?> apiClass) throws NoSuchMethodException, SecurityException, PermissionDeniedException {
        // 获取目标类
        boolean isAuth = false;
        List<String> authNameList = new ArrayList<>();
        if (apiClass != null) {
            //AuthAction action = apiClass.getAnnotation(AuthAction.class);
            AuthAction[] actions = apiClass.getAnnotationsByType(AuthAction.class);
            if (!Objects.equals(TenantContext.get().getTenantUuid(), "master") && actions.length > 0) {
                for (AuthAction action : actions) {
                    if (StringUtils.isNotBlank(action.action().getSimpleName())) {
                        String actionName = action.action().getSimpleName();
                        // 判断用户角色是否拥有接口权限
                        if (AuthActionChecker.check(actionName)) {
                            isAuth = true;
                            break;
                        }
                    }
                    authNameList.add($.t(AuthFactory.getAuthInstance(action.action().getSimpleName()).getAuthDisplayName()));
                }
            } else {
                isAuth = true;
            }

            if (!isAuth) {
                throw new PermissionDeniedException(authNameList);
            }
        }
    }


    protected void validApi(Class<?> apiClass, JSONObject paramObj, ApiVo apiVo, Class<?>... classes) throws NoSuchMethodException, SecurityException, PermissionDeniedException {
        // 获取目标类
        boolean isAuth = false;
        List<String> authNameList = new ArrayList<>();
        if (apiClass != null) {
            //AuthAction action = apiClass.getAnnotation(AuthAction.class);
            AuthAction[] actions = apiClass.getAnnotationsByType(AuthAction.class);
            if (!Objects.equals(TenantContext.get().getTenantUuid(), "master") && actions.length > 0) {
                for (AuthAction action : actions) {
                    if (StringUtils.isNotBlank(action.action().getSimpleName())) {
                        String actionName = action.action().getSimpleName();
                        // 判断用户角色是否拥有接口权限
                        if (AuthActionChecker.check(actionName)) {
                            isAuth = true;
                            break;
                        }
                    }
                    authNameList.add($.t(AuthFactory.getAuthInstance(action.action().getSimpleName()).getAuthDisplayName()));
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
            for (Param p : params) {
                if (p.type().equals(ApiParamType.NOAUTH)) {
                    continue;
                }
                // xss过滤
                if (p.xss() && paramObj.containsKey(p.name())) {
                    XssUtil.escapeXss(paramObj, p.name());
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
                if (p.isRequired() && !p.type().equals(ApiParamType.FILE)) {
                    if (!paramObj.containsKey(p.name())) {
                        throw new ParamNotExistsException(p.name());
                    } else {
                        if (paramValue != null && StringUtils.isBlank(paramValue.toString())) {
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
                        } else if (p.type().equals(ApiParamType.ENUM)) {
                            paramObj.put(p.name(), p.defaultValue());
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
                if (p.maxSize() > 0) {
                    if (paramValue instanceof JSONArray) {
                        JSONArray paramArray = JSONArray.parseArray(paramValue.toString());
                        if (paramArray.size() > p.maxSize()) {
                            throw new ParamValueTooLongException(p.name(), paramArray.size(), p.maxSize());
                        }
                    }
                    if (paramValue instanceof JSONObject) {
                        JSONObject paramJson = JSONObject.parseObject(paramValue.toString());
                        if (paramJson.size() > p.maxSize()) {
                            throw new ParamValueTooLongException(p.name(), paramJson.size(), p.maxSize());
                        }
                    }
                }

                // 判断最小长度
                if (p.minLength() > 0 && paramValue instanceof String) {
                    if (paramValue.toString().trim().length() < p.minLength()) {
                        throw new ParamValueTooShortException(p.name(), paramValue.toString().length(), p.minLength());
                    }
                }
                if (p.minSize() > 0) {
                    if (paramValue instanceof JSONArray) {
                        JSONArray paramArray = JSONArray.parseArray(paramValue.toString());
                        if (paramArray.size() < p.minSize()) {
                            throw new ParamValueTooShortException(p.name(), paramArray.size(), p.minSize());
                        }
                    }
                    if (paramValue instanceof JSONObject) {
                        JSONObject paramJson = JSONObject.parseObject(paramValue.toString());
                        if (paramJson.size() < p.minSize()) {
                            throw new ParamValueTooShortException(p.name(), paramJson.size(), p.minSize());
                        }
                    }
                }
                if (paramValue != null) {
                    String rule;
                    // 如果注解中存在member字段，则以引用的枚举值作为合法输入值
                    if (ApiParamType.ENUM.equals(p.type()) && p.member() != NotDefined.class) {
                        rule = getEnumMember(p);
                    } else {
                        rule = p.rule();
                    }
                    if (!ParamValidatorFactory.getAuthInstance(p.type()).validate(paramValue, rule)) {
                        throw new ParamIrregularException($.t(p.desc()) + "（" + p.name() + "）");
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
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(EntityField.class)) {
                EntityField entityField = (EntityField) annotation;
                JSONObject paramObj = new JSONObject();
                paramObj.put("name", field.getName());
                paramObj.put("type", entityField.type().getValue() + "[" + entityField.type().getText() + "]");
                paramObj.put("description", $.t(entityField.name()));

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
                        for (Param p : params) {
                            JSONObject paramObj = new JSONObject();
                            paramObj.put("name", p.name());
                            if (p.maxLength() > 0) {
                                paramObj.put("maxLength", p.maxLength());
                            }
                            paramObj.put("type", p.type().getValue() + "[" + p.type().getText() + "]");
                            String rule = "";
                            if (ApiParamType.ENUM.equals(p.type()) && p.member() != NotDefined.class) {
                                rule = getEnumMember(p);
                            } else if (StringUtils.isNotBlank(p.rule())) {
                                rule = p.rule();
                            }
                            paramObj.put("rule", rule);
                            paramObj.put("isRequired", p.isRequired());
                            if (StringUtils.isNotBlank(p.desc())) {
                                paramObj.put("description", $.t(p.desc()));
                            }
                            if (StringUtils.isNotBlank(p.help())) {
                                paramObj.put("help", $.t(p.help()));
                            }
                            inputList.add(paramObj);
                        }
                    } else if (anno.annotationType().equals(Output.class)) {
                        Output output = (Output) anno;
                        Param[] params = output.value();
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
                                    if (StringUtils.isNotBlank(p.desc())) {
                                        paramObj.put("description", p.desc());
                                    }
                                    if (StringUtils.isNotBlank(p.help())) {
                                        paramObj.put("help", p.help());
                                    }
                                    JSONArray elementObjList = new JSONArray();
                                    for (Field field : p.explode().getComponentType().getDeclaredFields()) {
                                        drawFieldMessageRecursive(field, elementObjList, true);
                                    }
                                    if (!elementObjList.isEmpty()) {
                                        paramObj.put("children", elementObjList);
                                    }

                                    outputList.add(paramObj);
                                }
                            } else {
                                JSONObject paramObj = new JSONObject();
                                paramObj.put("name", p.name());
                                paramObj.put("type", p.type().getValue() + "[" + p.type().getText() + "]");
                                paramObj.put("description", $.t(p.desc()));
                                paramObj.put("help", $.t(p.help()));
                                outputList.add(paramObj);
                            }
                        }
                    } else if (anno.annotationType().equals(Description.class)) {
                        Description description = (Description) anno;
                        jsonObj.put("description", $.t(description.desc()));
                    } else if (anno.annotationType().equals(Example.class)) {
                        Example example = (Example) anno;
                        String content = example.example();
                        if (StringUtils.isNotBlank(content)) {
                            try {
                                jsonObj.put("example", JSONObject.parseObject(content));
                            } catch (Exception ex) {
                                try {
                                    jsonObj.put("example", JSONArray.parseArray(content));
                                } catch (Exception ignored) {

                                }
                            }

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
            CacheControl cacheControl;
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

    /**
     * 获取注解中引用的枚举值
     */
    private String getEnumMember(Param p) {
        try {
            if (!p.member().isInterface()) {
                Object[] objects = p.member().getEnumConstants();
                List<String> valueList = new ArrayList<>();
                for (Object object : objects) {
                    String value = ((IEnum) object).getValue();
                    if (value != null) {
                        valueList.add(value);
                    }
                }
                if (valueList.size() > 0) {
                    return String.join(",", valueList);
                }
            } else {
                Reflections reflections = ReflectionManager.getInstance();
                List<String> valueList = new ArrayList<>();
                for (Class<?> cls : reflections.getSubTypesOf(p.member())) {
                    if (!cls.isInterface()) {
                        Object[] objects = cls.getEnumConstants();

                        for (Object object : objects) {
                            String value = ((IEnum) object).getValue();
                            if (value != null) {
                                valueList.add(value);
                            }
                        }

                    }
                }
                if (valueList.size() > 0) {
                    return String.join(",", valueList);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }
}
