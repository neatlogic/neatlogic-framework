/*
 * Copyright(c) 2023 NeatLogic Co., Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package neatlogic.framework.restful.core.privateapi;

import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.auth.core.AuthActions;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.restful.annotation.OperationType;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.IBinaryStreamApiComponent;
import neatlogic.framework.restful.core.IJsonStreamApiComponent;
import neatlogic.framework.restful.dto.ApiHandlerVo;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiKind;
import neatlogic.framework.restful.enums.ApiType;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RootComponent
public class PrivateApiComponentFactory extends ModuleInitializedListenerBase {
    static Logger logger = LoggerFactory.getLogger(PrivateApiComponentFactory.class);

    private static final Map<String, IApiComponent> componentMap = new HashMap<>();
    private static final List<ApiHandlerVo> apiHandlerList = new ArrayList<>();
    private static final Map<String, ApiHandlerVo> apiHandlerMap = new HashMap<>();
    private static final List<ApiVo> apiList = new ArrayList<>();
    private static final Map<String, ApiVo> apiMap = new HashMap<>();
    // public static Map<String, RateLimiter> interfaceRateMap = new
    // ConcurrentHashMap<>();
    private static final Map<String, IJsonStreamApiComponent> streamComponentMap = new HashMap<>();
    private static final Map<String, IBinaryStreamApiComponent> binaryComponentMap = new HashMap<>();
    // ??????token??????????????????????????????????????????
    private static final Map<String, ApiVo> regexApiMap = new TreeMap<>((o1, o2) -> {
        // ????????????????????????????????????????????????????????????
        if (o1.length() != o2.length()) {
            return o1.length() - o2.length();
        } else {
            return o1.compareTo(o2);
        }
    });
    private static final Pattern p = Pattern.compile("\\{([^}]+)}");

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
            for (String regex : regexApiMap.keySet()) {
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

    public static Map<String, IBinaryStreamApiComponent> getBinaryStreamComponentMap() {
        return binaryComponentMap;
    }

    public static Map<String, IJsonStreamApiComponent> getJsonStreamComponentMap() {
        return streamComponentMap;
    }

    public static Map<String, ApiHandlerVo> getApiHandlerMap() {
        return apiHandlerMap;
    }

    public static Map<String, ApiVo> getApiMap() {
        return apiMap;
    }

    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IPrivateApiComponent> myMap = context.getBeansOfType(IPrivateApiComponent.class);
        Map<String, IPrivateJsonStreamApiComponent> myStreamMap = context.getBeansOfType(IPrivateJsonStreamApiComponent.class);
        Map<String, IPrivateBinaryStreamApiComponent> myBinaryMap = context.getBeansOfType(IPrivateBinaryStreamApiComponent.class);
        for (Map.Entry<String, IPrivateApiComponent> entry : myMap.entrySet()) {
            IPrivateApiComponent component = entry.getValue();
            if (component.getClassName() != null) {
                checkAnnotation(component, context);
                componentMap.put(component.getClassName(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getClassName());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(true);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiType.OBJECT.getValue());
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
                    apiVo.setTimeout(0);// 0???default
                    apiVo.setType(ApiType.OBJECT.getValue());
                    apiVo.setModuleId(context.getId());
                    apiVo.setModuleGroup(context.getGroup());//??????moduleId??????moduleGroup
                    apiVo.setApiType(ApiKind.SYSTEM.getValue());// ???????????????????????????????????????
                    apiVo.setIsDeletable(0);// ????????????
                    apiVo.setIsPrivate(true);
                    if (token.contains("{")) {
                        Matcher m = p.matcher(token);
                        StringBuffer temp = new StringBuffer();
                        while (m.find()) {
                            apiVo.addPathVariable(m.group(1));
                            m.appendReplacement(temp, "([^\\/]+)");
                        }
                        m.appendTail(temp);
                        String regexToken = "^" + temp + "$";
                        if (!regexApiMap.containsKey(regexToken)) {
                            regexApiMap.put(regexToken, apiVo);
                        } else {
                            logger.error("?????????????????????" + regexToken + "  " + token + "???????????????????????????????????????");
                            System.exit(1);
                        }
                    }
                    // ?????????regex path???????????????apiMap????????????????????????????????????
                    if (!apiMap.containsKey(token)) {
                        apiList.add(apiVo);
                        apiMap.put(token, apiVo);
                    } else {
                        logger.error("?????????" + token + "???????????????????????????????????????");
                        System.exit(1);
                    }

                }
            }
        }

        for (Map.Entry<String, IPrivateJsonStreamApiComponent> entry : myStreamMap.entrySet()) {
            IPrivateJsonStreamApiComponent component = entry.getValue();
            if (component.getId() != null) {
                checkAnnotation(component, context);
                streamComponentMap.put(component.getId(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getId());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(true);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiType.STREAM.getValue());
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
                    apiVo.setTimeout(0);// 0???default
                    apiVo.setType(ApiType.STREAM.getValue());
                    apiVo.setModuleId(context.getId());
                    apiVo.setModuleGroup(context.getGroup());//??????moduleId??????moduleGroup
                    apiVo.setApiType(ApiKind.SYSTEM.getValue());// ???????????????????????????????????????
                    apiVo.setIsDeletable(0);// ????????????
                    apiVo.setIsPrivate(true);

                    if (token.contains("{")) {
                        Matcher m = p.matcher(token);
                        StringBuffer temp = new StringBuffer();
                        while (m.find()) {
                            apiVo.addPathVariable(m.group(1));
                            m.appendReplacement(temp, "([^\\/]+)");
                        }
                        m.appendTail(temp);
                        String regexToken = "^" + temp + "$";
                        if (!regexApiMap.containsKey(regexToken)) {
                            regexApiMap.put(regexToken, apiVo);
                        } else {
                            logger.error("?????????????????????" + regexToken + "  " + token + "???????????????????????????????????????");
                            System.exit(1);
                        }
                    }

                    if (!apiMap.containsKey(token)) {
                        apiList.add(apiVo);
                        apiMap.put(token, apiVo);
                    } else {
                        logger.error("?????????" + token + "???????????????????????????????????????");
                        System.exit(1);
                    }
                }
            }
        }

        for (Map.Entry<String, IPrivateBinaryStreamApiComponent> entry : myBinaryMap.entrySet()) {
            IPrivateBinaryStreamApiComponent component = entry.getValue();
            if (component.getId() != null) {
                checkAnnotation(component, context);
                binaryComponentMap.put(component.getId(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getId());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(true);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiType.BINARY.getValue());
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
                    apiVo.setTimeout(0);// 0???default
                    apiVo.setType(ApiType.BINARY.getValue());
                    apiVo.setModuleId(context.getId());
                    apiVo.setModuleGroup(context.getGroup());//??????moduleId??????moduleGroup
                    apiVo.setApiType(ApiKind.SYSTEM.getValue());// ???????????????????????????????????????
                    apiVo.setIsDeletable(0);// ????????????
                    apiVo.setIsPrivate(true);

                    if (token.contains("{")) {
                        Matcher m = p.matcher(token);
                        StringBuffer temp = new StringBuffer();
                        while (m.find()) {
                            apiVo.addPathVariable(m.group(1));
                            m.appendReplacement(temp, "([^\\/]+)");
                        }
                        m.appendTail(temp);
                        String regexToken = "^" + temp + "$";
                        if (!regexApiMap.containsKey(regexToken)) {
                            regexApiMap.put(regexToken, apiVo);
                        } else {
                            logger.error("?????????????????????" + regexToken + "  " + token + "???????????????????????????????????????");
                            System.exit(1);
                        }
                    }

                    if (!apiMap.containsKey(token)) {
                        apiList.add(apiVo);
                        apiMap.put(token, apiVo);
                    } else {
                        logger.error("?????????" + token + "???????????????????????????????????????");
                        System.exit(1);
                    }
                }
            }
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param component ??????
     * @param context   ??????context
     */
    public void checkAnnotation(Object component, ApplicationContext context) {
        //TODO ??????master?????????????????????
        if (!Objects.equals(context.getId(), "master")) {
            Class<?> clazz = AopUtils.getTargetClass(component);
            OperationType operationType = clazz.getAnnotation(OperationType.class);
            if (operationType == null) {
                logger.warn(clazz.getName() + "????????????OperationType??????");
            }

            //System.out.println(clazz.getSimpleName());
            if (!Objects.equals(context.getId(), "framework") && !Objects.equals(context.getId(), "tenant")) {
                AuthAction authAction = clazz.getAnnotation(AuthAction.class);
                AuthActions authActions = clazz.getAnnotation(AuthActions.class);
                if (authAction == null && authActions == null) {
                    logger.warn(clazz.getName() + "????????????AuthAction??????");
                }
            }
        }
    }

    @Override
    protected void myInit() {

    }

    public static List<ApiVo> getTenantActiveApiList() {
        List<String> activeModuleIdList = TenantContext.get().getActiveModuleList().stream().map(ModuleVo::getId).collect(Collectors.toList());
        return apiList.stream().filter(e -> activeModuleIdList.contains(e.getModuleId())).collect(Collectors.toList());
    }
}
