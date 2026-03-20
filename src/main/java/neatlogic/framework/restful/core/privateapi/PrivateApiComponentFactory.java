/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.restful.core.privateapi;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.auth.core.AuthAction;
import neatlogic.framework.auth.core.AuthActions;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.dto.module.ModuleVo;
import neatlogic.framework.restful.annotation.NoPasswordExpiredCheck;
import neatlogic.framework.restful.annotation.OperationType;
import neatlogic.framework.restful.constvalue.ApiAuthType;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.dto.ApiHandlerVo;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiKind;
import neatlogic.framework.restful.enums.ApiType;
import neatlogic.framework.util.$;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 私有接口组件工厂。
 * 负责在模块初始化时协调各类型注册器完成 private API 组件注册，并维护统一的处理器、token 与组件索引。
 */
@RootComponent
public class PrivateApiComponentFactory extends ModuleInitializedListenerBase {
    static Logger logger = LoggerFactory.getLogger(PrivateApiComponentFactory.class);

    @Resource
    private List<IPrivateApiTypeRegistrar> registrarList;

    private static final Map<String, Map<String, Object>> componentRegistryMap = new HashMap<>();
    private static final List<ApiHandlerVo> apiHandlerList = new ArrayList<>();
    private static final Map<String, ApiHandlerVo> apiHandlerMap = new HashMap<>();
    private static final List<ApiVo> apiList = new ArrayList<>();
    private static final Map<String, ApiVo> apiMap = new HashMap<>();
    public static final List<String> ExemptTokenMap = new ArrayList<>();
    // 按照token表达式长度排序，最长匹配原则
    private static final Map<String, ApiVo> regexApiMap = new TreeMap<>((o1, o2) -> {
        if (o1.length() != o2.length()) {
            return o1.length() - o2.length();
        } else {
            return o1.compareTo(o2);
        }
    });
    private static final Pattern p = Pattern.compile("\\{([^}]+)}");

    static {
        for (ApiType apiType : ApiType.values()) {
            componentRegistryMap.put(apiType.getValue(), new HashMap<>());
        }
    }

    /**
     * 按接口类型和处理器类名获取组件实例。
     */
    public static <T> T getComponent(String componentId, ApiType apiType, Class<T> componentClass) {
        Object component = getTypedComponentMap(apiType).get(componentId);
        if (component == null) {
            return null;
        }
        return componentClass.cast(component);
    }

    /**
     * 获取当前已注册且声明为 MCP 工具的接口列表。
     */
    public static List<ApiVo> getMcpApiList() {
        return apiList.stream().filter(ApiVo::getIsMcp).toList();
    }

    /**
     * 根据 token 获取接口定义。
     * 优先命中精确 token，未命中时再尝试正则 token，并回填路径变量。
     */
    public static ApiVo getApiByToken(String token) throws CloneNotSupportedException {
        ApiVo api = apiMap.get(token);
        if (api == null) {
            for (String regex : regexApiMap.keySet()) {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(token);
                if (matcher.find()) {
                    api = regexApiMap.get(regex);
                    if (api.getPathVariableList() != null
                            && api.getPathVariableList().size() == matcher.groupCount()) {
                        JSONObject pathVariableObj = new JSONObject();
                        for (int i = 0; i < api.getPathVariableList().size(); i++) {
                            try {
                                pathVariableObj.put(api.getPathVariableList().get(i), matcher.group(i + 1));
                            } catch (JSONException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                        api.setPathVariableObj(pathVariableObj);
                    }
                    break;
                }
            }
        }
        ApiVo apiVo = null;
        if (api != null) {
            apiVo = api.clone();
            apiVo.setName($.t(apiVo.getName()));
            apiVo.setHandlerName($.t(apiVo.getHandlerName()));
        }
        return apiVo;
    }

    /**
     * 获取全部已注册的接口定义。
     */
    public static List<ApiVo> getApiList() {
        return apiList;
    }

    /**
     * 根据处理器类名获取处理器元数据。
     */
    public static ApiHandlerVo getApiHandlerByHandler(String handler) {
        return apiHandlerMap.get(handler);
    }

    /**
     * 获取全部处理器元数据。
     */
    public static List<ApiHandlerVo> getApiHandlerList() {
        return apiHandlerList;
    }

    /**
     * 获取对象型接口组件索引。
     */
    public static Map<String, IApiComponent> getComponentMap() {
        return getTypedComponentMap(ApiType.OBJECT);
    }

    /**
     * 获取处理器元数据索引。
     */
    public static Map<String, ApiHandlerVo> getApiHandlerMap() {
        return apiHandlerMap;
    }

    /**
     * 获取接口定义索引。
     */
    public static Map<String, ApiVo> getApiMap() {
        return apiMap;
    }

    /**
     * 统一规范化接口 token，移除首尾斜杠。
     */
    static String normalizeToken(String token) {
        if (token.startsWith("/")) {
            token = token.substring(1);
        }
        if (token.endsWith("/")) {
            token = token.substring(0, token.length() - 1);
        }
        return token;
    }

    /**
     * 构建处理器元数据对象。
     */
    static ApiHandlerVo createApiHandlerVo(String className, String name, String config, String moduleId, ApiType apiType) {
        ApiHandlerVo restComponentVo = new ApiHandlerVo();
        restComponentVo.setHandler(className);
        restComponentVo.setName(name);
        restComponentVo.setConfig(config);
        restComponentVo.setPrivate(true);
        restComponentVo.setModuleId(moduleId);
        restComponentVo.setType(apiType.getValue());
        return restComponentVo;
    }

    /**
     * 注册处理器元数据到列表和索引。
     */
    static void registerApiHandler(ApiHandlerVo apiHandlerVo) {
        apiHandlerList.add(apiHandlerVo);
        apiHandlerMap.put(apiHandlerVo.getHandler(), apiHandlerVo);
    }

    /**
     * 构建系统级 private 接口定义。
     */
    static ApiVo createSystemApiVo(String token, String className, String name, String description, NeatLogicWebApplicationContext context,
                                   ApiType apiType, Integer needAudit, boolean isMcp, boolean isBasicSupport, boolean supportAnonymousAccess) {
        ApiVo apiVo = new ApiVo();
        apiVo.setToken(token);
        apiVo.setHandler(className);
        apiVo.setHandlerName(name);
        apiVo.setName(name);
        apiVo.setDescription(description);
        apiVo.setIsActive(1);
        apiVo.setIsMcp(isMcp);
        apiVo.setNeedAudit(needAudit);
        apiVo.setTimeout(0);
        apiVo.setType(apiType.getValue());
        apiVo.setModuleId(context.getId());
        apiVo.setModuleGroup(context.getGroup());
        apiVo.setApiType(ApiKind.SYSTEM.getValue());
        apiVo.setIsDeletable(0);
        apiVo.setIsPrivate(true);
        if (isBasicSupport) {
            apiVo.addAuthType(ApiAuthType.BASIC.getValue());
        }
        if (supportAnonymousAccess) {
            apiVo.addAuthType(ApiAuthType.ANONYMOUS.getValue());
        }
        return apiVo;
    }

    /**
     * 注册接口 token。
     * 同时维护精确 token 映射、正则 token 映射以及接口列表。
     */
    static void registerApiToken(String token, ApiVo apiVo) {
        if (token.contains("{")) {
            Matcher m = p.matcher(token);
            StringBuffer temp = new StringBuffer();
            while (m.find()) {
                apiVo.addPathVariable(m.group(1));
                m.appendReplacement(temp, "([^/]+)");
            }
            m.appendTail(temp);
            String regexToken = "^" + temp + "$";
            if (!regexApiMap.containsKey(regexToken)) {
                regexApiMap.put(regexToken, apiVo);
            } else {
                logger.error("路径匹配接口：" + regexToken + "  " + token + "已存在，请重新定义访问路径");
                System.exit(1);
            }
        }
        if (!apiMap.containsKey(token)) {
            apiList.add(apiVo);
            apiMap.put(token, apiVo);
        } else {
            logger.error("接口：" + token + "已存在，请重新定义访问路径");
            System.exit(1);
        }
    }

    /**
     * 按接口类型注册组件实例。
     */
    static void registerComponent(ApiType apiType, String className, Object component) {
        getTypedComponentMap(apiType).put(className, component);
    }

    /**
     * 获取指定接口类型的组件索引。
     */
    @SuppressWarnings("unchecked")
    static <T> Map<String, T> getTypedComponentMap(ApiType apiType) {
        return (Map<String, T>) (Map<?, ?>) componentRegistryMap.computeIfAbsent(apiType.getValue(), key -> new HashMap<>());
    }

    /**
     * 模块初始化入口。
     * 统一调度各类型注册器完成 private API 注册。
     */
    @Override
    public void onInitialized(NeatLogicWebApplicationContext context) {
        registrarList.stream()
                .sorted(Comparator.comparingInt(IPrivateApiTypeRegistrar::getOrder)
                        .thenComparing(registrar -> registrar.getClass().getName()))
                .forEach(registrar -> registrar.register(context));
    }

    /**
     * 补充注解提示，防止越权。
     */
    static void checkAnnotation(Object component, ApplicationContext context) {
        if (!Objects.equals(context.getId(), "master")) {
            Class<?> clazz = AopUtils.getTargetClass(component);
            OperationType operationType = clazz.getAnnotation(OperationType.class);
            if (operationType == null) {
                logger.warn("{}接口没有OperationType注解", clazz.getName());
            }
            AuthAction authAction = clazz.getAnnotation(AuthAction.class);
            AuthActions authActions = clazz.getAnnotation(AuthActions.class);
            if (authAction == null && authActions == null) {
                System.err.println(clazz.getName() + "接口类需要加上@AuthAction注解进行权限控制, 如果未创建权限类, 可以先临时加上@AuthAction(action = NoAuth.class)使得应用服务正常启动");
                System.exit(1);
            }
            if (clazz.isAnnotationPresent(NoPasswordExpiredCheck.class)) {
                if (component instanceof IPrivateApiComponent) {
                    ExemptTokenMap.add(((IPrivateApiComponent) component).getToken());
                }
            }
        }
    }

    /**
     * 预留初始化钩子，当前无额外初始化逻辑。
     */
    @Override
    protected void myInit() {

    }

    /**
     * 获取当前租户已激活模块下可见的接口定义列表。
     * 返回值为克隆副本，避免调用方修改全局缓存对象。
     */
    public static List<ApiVo> getTenantActiveApiList() throws CloneNotSupportedException {
        List<String> activeModuleIdList = TenantContext.get().getActiveModuleList().stream().map(ModuleVo::getId).collect(Collectors.toList());
        List<ApiVo> apiVoList = apiList.stream().filter(e -> activeModuleIdList.contains(e.getModuleId())).collect(Collectors.toList());
        List<ApiVo> clonedList = new ArrayList<>();
        for (ApiVo apiVo : apiVoList) {
            clonedList.add(apiVo.clone());
        }
        clonedList.forEach(a -> {
            a.setName($.t(a.getName()));
            a.setHandlerName($.t(a.getHandlerName()));
        });
        return clonedList;
    }
}
