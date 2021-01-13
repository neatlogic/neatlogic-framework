package codedriver.framework.restful.core.privateapi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.auth.core.AuthAction;
import codedriver.framework.common.RootComponent;
import codedriver.framework.restful.core.constvalue.OperationTypeEnum;
import codedriver.framework.restful.annotation.OperationType;
import codedriver.framework.restful.core.IApiComponent;
import codedriver.framework.restful.core.IBinaryStreamApiComponent;
import codedriver.framework.restful.core.IJsonStreamApiComponent;
import codedriver.framework.restful.dto.ApiHandlerVo;
import codedriver.framework.restful.dto.ApiVo;

@RootComponent
public class PrivateApiComponentFactory implements ApplicationListener<ContextRefreshedEvent> {
    static Logger logger = LoggerFactory.getLogger(PrivateApiComponentFactory.class);

    private static Map<String, IApiComponent> componentMap = new HashMap<>();
    private static List<ApiHandlerVo> apiHandlerList = new ArrayList<>();
    private static Map<String, ApiHandlerVo> apiHandlerMap = new HashMap<>();
    private static List<ApiVo> apiList = new ArrayList<>();
    private static Map<String, ApiVo> apiMap = new HashMap<>();
    // public static Map<String, RateLimiter> interfaceRateMap = new
    // ConcurrentHashMap<>();
    private static Map<String, IJsonStreamApiComponent> streamComponentMap = new HashMap<>();
    private static Map<String, IBinaryStreamApiComponent> binaryComponentMap = new HashMap<>();
    // 按照token表达式长度排序，最长匹配原则
    private static Map<String, ApiVo> regexApiMap = new TreeMap<String, ApiVo>(new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            // 先按照长度排序，如果长度一样按照内容排序
            if (o1.length() != o2.length()) {
                return o1.length() - o2.length();
            } else {
                return o1.compareTo(o2);
            }
        }
    });
    private static Pattern p = Pattern.compile("\\{([^}]+)\\}");

    public static IApiComponent getInstance(String componentId) {
        return componentMap.get(componentId);
    }

    public static IJsonStreamApiComponent getStreamInstance(String componentId) {
        return streamComponentMap.get(componentId);
    }

    public static IBinaryStreamApiComponent getBinaryInstance(String componentId) {
        return binaryComponentMap.get(componentId);
    }

    public static ApiVo getApiByToken(String token) {
        ApiVo apiVo = apiMap.get(token);
        if (apiVo == null) {
            Iterator<String> keys = regexApiMap.keySet().iterator();
            while (keys.hasNext()) {
                String regex = keys.next();
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(token);
                if (matcher.find()) {
                    apiVo = regexApiMap.get(regex);
                    if (apiVo.getPathVariableList() != null
                        && apiVo.getPathVariableList().size() == matcher.groupCount()) {
                        JSONObject pathVariableObj = new JSONObject();
                        for (int i = 0; i < apiVo.getPathVariableList().size(); i++) {
                            try {
                                pathVariableObj.put(apiVo.getPathVariableList().get(i), matcher.group(i + 1));
                            } catch (JSONException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                        apiVo.setPathVariableObj(pathVariableObj);
                    }
                    break;
                }
            }
        }
        return apiVo;
    }

    public static List<ApiVo> getApiList() {
        return apiList;
    }

    public static ApiHandlerVo getApiHandlerByHandler(String handler) {
        return apiHandlerMap.get(handler);
    }

    public static List<ApiHandlerVo> getApiHandlerList() {
        return apiHandlerList;
    }

    public static Map<String, IApiComponent> getComponentMap() {
        return componentMap;
    }

    public static Map<String, IJsonStreamApiComponent> getStreamComponentMap() {
        return streamComponentMap;
    }

    public static Map<String, IBinaryStreamApiComponent> getBinaryComponentMap() {
        return binaryComponentMap;
    }

    public static Map<String, ApiHandlerVo> getApiHandlerMap() {
        return apiHandlerMap;
    }

    public static Map<String, ApiVo> getApiMap() {
        return apiMap;
    }

    @SuppressWarnings("unused")
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IPrivateApiComponent> myMap = context.getBeansOfType(IPrivateApiComponent.class);
        Map<String, IPrivateJsonStreamApiComponent> myStreamMap = context.getBeansOfType(IPrivateJsonStreamApiComponent.class);
        Map<String, IPrivateBinaryStreamApiComponent> myBinaryMap = context.getBeansOfType(IPrivateBinaryStreamApiComponent.class);
        for (Map.Entry<String, IPrivateApiComponent> entry : myMap.entrySet()) {
            IPrivateApiComponent component = entry.getValue();
            if (component.getClassName() != null) {
                componentMap.put(component.getClassName(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getClassName());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(true);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiVo.Type.OBJECT.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getClassName(), restComponentVo);
                String token = component.getToken();
                if (StringUtils.isNotBlank(token)) {
                    if (token.startsWith("/")) {
                        token = token.substring(1);
                    }
                    if (token.endsWith("/")) {
                        token = token.substring(0, token.length() - 1);
                    }
                    ApiVo apiVo = new ApiVo();
                    apiVo.setAuthtype("token");
                    apiVo.setToken(token);
                    apiVo.setHandler(component.getClassName());
                    apiVo.setHandlerName(component.getName());
                    apiVo.setName(component.getName());
                    // Class<?> targetClass = AopUtils.getTargetClass(component);
                    // if (targetClass.getAnnotation(IsActived.class) != null) {
                    // apiVo.setIsActive(1);
                    // } else {
                    // apiVo.setIsActive(0);
                    // }
                    apiVo.setIsActive(1);
                    apiVo.setNeedAudit(component.needAudit());
                    apiVo.setTimeout(0);// 0是default
                    apiVo.setType(ApiVo.Type.OBJECT.getValue());
                    apiVo.setModuleId(context.getId());
                    // apiVo.getModuleGroup(context.getId());//根据moduleId设置moduleGroup
                    apiVo.setApiType(ApiVo.ApiType.SYSTEM.getValue());// 系统扫描出来的就是系统接口
                    apiVo.setIsDeletable(0);// 不能删除
                    apiVo.setIsPrivate(true);
                    if (token.indexOf("{") > -1) {
                        Matcher m = p.matcher(token);
                        StringBuffer temp = new StringBuffer();
                        int i = 0;
                        while (m.find()) {
                            apiVo.addPathVariable(m.group(1));
                            m.appendReplacement(temp, "([^\\/]+)");
                            i++;
                        }
                        m.appendTail(temp);
                        String regexToken = "^" + temp.toString() + "$";
                        if (!regexApiMap.containsKey(regexToken)) {
                            regexApiMap.put(regexToken, apiVo);
                        } else {
                            throw new RuntimeException("路径匹配接口：" + regexToken + "  " + token + "已存在，请重新定义访问路径");
                        }
                    }
                    // 即使是regex path也需要存到apiMap里，这样才能获取帮助信息
                    if (!apiMap.containsKey(token)) {
                        apiList.add(apiVo);
                        apiMap.put(token, apiVo);
                    } else {
                        throw new RuntimeException("接口：" + token + "已存在，请重新定义访问路径");
                    }

                }
            }
        }

        for (Map.Entry<String, IPrivateJsonStreamApiComponent> entry : myStreamMap.entrySet()) {
            IPrivateJsonStreamApiComponent component = entry.getValue();
            if (component.getId() != null) {
                streamComponentMap.put(component.getId(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getId());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(true);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiVo.Type.STREAM.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getId(), restComponentVo);
                String token = component.getToken();
                if (StringUtils.isNotBlank(token)) {
                    if (token.startsWith("/")) {
                        token = token.substring(1);
                    }
                    if (token.endsWith("/")) {
                        token = token.substring(0, token.length() - 1);
                    }
                    ApiVo apiVo = new ApiVo();
                    apiVo.setAuthtype("token");
                    apiVo.setToken(token);
                    apiVo.setHandler(component.getId());
                    apiVo.setHandlerName(component.getName());
                    apiVo.setName(component.getName());
                    // Class<?> targetClass = AopUtils.getTargetClass(component);
                    // if (targetClass.getAnnotation(IsActived.class) != null) {
                    // apiVo.setIsActive(1);
                    // } else {
                    // apiVo.setIsActive(0);
                    // }
                    apiVo.setIsActive(1);
                    apiVo.setNeedAudit(component.needAudit());
                    apiVo.setTimeout(0);// 0是default
                    apiVo.setType(ApiVo.Type.STREAM.getValue());
                    apiVo.setModuleId(context.getId());
                    // apiVo.getModuleGroup(context.getId());//根据moduleId设置moduleGroup
                    apiVo.setApiType(ApiVo.ApiType.SYSTEM.getValue());// 系统扫描出来的就是系统接口
                    apiVo.setIsDeletable(0);// 不能删除
                    apiVo.setIsPrivate(true);

                    if (token.indexOf("{") > -1) {
                        Matcher m = p.matcher(token);
                        StringBuffer temp = new StringBuffer();
                        int i = 0;
                        while (m.find()) {
                            apiVo.addPathVariable(m.group(1));
                            m.appendReplacement(temp, "([^\\/]+)");
                            i++;
                        }
                        m.appendTail(temp);
                        String regexToken = "^" + temp.toString() + "$";
                        if (!regexApiMap.containsKey(regexToken)) {
                            regexApiMap.put(regexToken, apiVo);
                        } else {
                            throw new RuntimeException("路径匹配接口：" + token + "已存在，请重新定义访问路径");
                        }
                    }

                    if (!apiMap.containsKey(token)) {
                        apiList.add(apiVo);
                        apiMap.put(token, apiVo);
                    } else {
                        throw new RuntimeException("接口：" + token + "已存在，请重新定义访问路径");
                    }
                }
            }
        }

        for (Map.Entry<String, IPrivateBinaryStreamApiComponent> entry : myBinaryMap.entrySet()) {
            IPrivateBinaryStreamApiComponent component = entry.getValue();
            if (component.getId() != null) {
                checkAnnotation(component);
                binaryComponentMap.put(component.getId(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getId());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(true);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiVo.Type.BINARY.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getId(), restComponentVo);
                String token = component.getToken();
                if (StringUtils.isNotBlank(token)) {
                    if (token.startsWith("/")) {
                        token = token.substring(1);
                    }
                    if (token.endsWith("/")) {
                        token = token.substring(0, token.length() - 1);
                    }
                    ApiVo apiVo = new ApiVo();
                    apiVo.setAuthtype("token");
                    apiVo.setToken(token);
                    apiVo.setHandler(component.getId());
                    apiVo.setHandlerName(component.getName());
                    apiVo.setName(component.getName());
                    // Class<?> targetClass = AopUtils.getTargetClass(component);
                    // if (targetClass.getAnnotation(IsActived.class) != null) {
                    // apiVo.setIsActive(1);
                    // } else {
                    // apiVo.setIsActive(0);
                    // }
                    apiVo.setIsActive(1);
                    apiVo.setNeedAudit(component.needAudit());
                    apiVo.setTimeout(0);// 0是default
                    apiVo.setType(ApiVo.Type.BINARY.getValue());
                    apiVo.setModuleId(context.getId());
                    // apiVo.getModuleGroup(context.getId());//根据moduleId设置moduleGroup
                    apiVo.setApiType(ApiVo.ApiType.SYSTEM.getValue());// 系统扫描出来的就是系统接口
                    apiVo.setIsDeletable(0);// 不能删除
                    apiVo.setIsPrivate(true);

                    if (token.indexOf("{") > -1) {
                        Matcher m = p.matcher(token);
                        StringBuffer temp = new StringBuffer();
                        int i = 0;
                        while (m.find()) {
                            apiVo.addPathVariable(m.group(1));
                            m.appendReplacement(temp, "([^\\/]+)");
                            i++;
                        }
                        m.appendTail(temp);
                        String regexToken = "^" + temp.toString() + "$";
                        if (!regexApiMap.containsKey(regexToken)) {
                            regexApiMap.put(regexToken, apiVo);
                        } else {
                            throw new RuntimeException("路径匹配接口：" + token + "已存在，请重新定义访问路径");
                        }
                    }

                    if (!apiMap.containsKey(token)) {
                        apiList.add(apiVo);
                        apiMap.put(token, apiVo);
                    } else {
                        throw new RuntimeException("接口：" + token + "已存在，请重新定义访问路径");
                    }
                }
            }
        }
    }
    
    /**
    * @Time 2020年12月22日  
    * @Description: 补充注解提示，防止越权
    * @Param 
    * @return
     */
    public void checkAnnotation(Object component) {
        Class<?> clazz = AopUtils.getTargetClass(component);
        OperationType operationType = clazz.getAnnotation(OperationType.class);
        if(operationType == null) {
            logger.error(clazz.getName() + "接口没有OperationType注解");
        }else {
            OperationTypeEnum operationTypeEnum = operationType.type();
            if(operationTypeEnum == OperationTypeEnum.DELETE || operationTypeEnum == OperationTypeEnum.CREATE || operationTypeEnum == OperationTypeEnum.UPDATE|| operationTypeEnum == OperationTypeEnum.OPERATE) {
                AuthAction authAction = clazz.getAnnotation(AuthAction.class);
                if(authAction == null) {
                    logger.error(clazz.getName() + "接口没有AuthAction注解");
                }
            }
        }      
    }
}
