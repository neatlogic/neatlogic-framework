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
import neatlogic.framework.restful.annotation.OperationType;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.IBinaryStreamApiComponent;
import neatlogic.framework.restful.core.IJsonStreamApiComponent;
import neatlogic.framework.restful.core.IRawApiComponent;
import neatlogic.framework.restful.dto.ApiHandlerVo;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiKind;
import neatlogic.framework.restful.enums.ApiType;
import neatlogic.framework.util.$;
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
    private static final Map<String, IJsonStreamApiComponent> streamComponentMap = new HashMap<>();
    private static final Map<String, IBinaryStreamApiComponent> binaryComponentMap = new HashMap<>();

    public static final Map<String, IRawApiComponent> rawComponentMap = new HashMap<>();
    // 按照token表达式长度排序，最长匹配原则
    private static final Map<String, ApiVo> regexApiMap = new TreeMap<>((o1, o2) -> {
        // 先按照长度排序，如果长度一样按照内容排序
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

    public static IRawApiComponent getRawInstance(String componentId) {
        return rawComponentMap.get(componentId);
    }

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
        Map<String, IPrivateRawApiComponent> myRawMap = context.getBeansOfType(IPrivateRawApiComponent.class);
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
                    apiVo.setIsActive(1);
                    apiVo.setNeedAudit(component.needAudit());
                    apiVo.setTimeout(0);// 0是default
                    apiVo.setType(ApiType.OBJECT.getValue());
                    apiVo.setModuleId(context.getId());
                    apiVo.setModuleGroup(context.getGroup());//根据moduleId设置moduleGroup
                    apiVo.setApiType(ApiKind.SYSTEM.getValue());// 系统扫描出来的就是系统接口
                    apiVo.setIsDeletable(0);// 不能删除
                    apiVo.setIsPrivate(true);
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
                    // 即使是regex path也需要存到apiMap里，这样才能获取帮助信息
                    if (!apiMap.containsKey(token)) {
                        apiList.add(apiVo);
                        apiMap.put(token, apiVo);
                    } else {
                        logger.error("接口：" + token + "已存在，请重新定义访问路径");
                        System.exit(1);
                    }

                }
            }
        }

        for (Map.Entry<String, IPrivateJsonStreamApiComponent> entry : myStreamMap.entrySet()) {
            IPrivateJsonStreamApiComponent component = entry.getValue();
            if (component.getClassName() != null) {
                checkAnnotation(component, context);
                streamComponentMap.put(component.getClassName(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getClassName());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(true);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiType.STREAM.getValue());
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
                    apiVo.setIsActive(1);
                    apiVo.setNeedAudit(component.needAudit());
                    apiVo.setTimeout(0);// 0是default
                    apiVo.setType(ApiType.STREAM.getValue());
                    apiVo.setModuleId(context.getId());
                    apiVo.setModuleGroup(context.getGroup());//根据moduleId设置moduleGroup
                    apiVo.setApiType(ApiKind.SYSTEM.getValue());// 系统扫描出来的就是系统接口
                    apiVo.setIsDeletable(0);// 不能删除
                    apiVo.setIsPrivate(true);

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
            }
        }

        for (Map.Entry<String, IPrivateBinaryStreamApiComponent> entry : myBinaryMap.entrySet()) {
            IPrivateBinaryStreamApiComponent component = entry.getValue();
            if (component.getClassName() != null) {
                checkAnnotation(component, context);
                binaryComponentMap.put(component.getClassName(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getClassName());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(true);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiType.BINARY.getValue());
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
                    apiVo.setIsActive(1);
                    apiVo.setNeedAudit(component.needAudit());
                    apiVo.setTimeout(0);// 0是default
                    apiVo.setType(ApiType.BINARY.getValue());
                    apiVo.setModuleId(context.getId());
                    apiVo.setModuleGroup(context.getGroup());//根据moduleId设置moduleGroup
                    apiVo.setApiType(ApiKind.SYSTEM.getValue());// 系统扫描出来的就是系统接口
                    apiVo.setIsDeletable(0);// 不能删除
                    apiVo.setIsPrivate(true);

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
            }
        }

        for (Map.Entry<String, IPrivateRawApiComponent> entry : myRawMap.entrySet()) {
            IPrivateRawApiComponent component = entry.getValue();
            if (component.getClassName() != null) {
                checkAnnotation(component, context);
                rawComponentMap.put(component.getClassName(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getClassName());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(true);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiType.RAW.getValue());
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
                    apiVo.setIsActive(1);
                    apiVo.setNeedAudit(component.needAudit());
                    apiVo.setTimeout(0);// 0是default
                    apiVo.setType(ApiType.RAW.getValue());
                    apiVo.setModuleId(context.getId());
                    apiVo.setModuleGroup(context.getGroup());//根据moduleId设置moduleGroup
                    apiVo.setApiType(ApiKind.SYSTEM.getValue());// 系统扫描出来的就是系统接口
                    apiVo.setIsDeletable(0);// 不能删除
                    apiVo.setIsPrivate(true);

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
            }
        }
    }

    /**
     * 补充注解提示，防止越权
     *
     * @param component 组件
     * @param context   应用context
     */
    public void checkAnnotation(Object component, ApplicationContext context) {
        //TODO 后续master模块完善后放开
        if (!Objects.equals(context.getId(), "master")) {
            Class<?> clazz = AopUtils.getTargetClass(component);
            OperationType operationType = clazz.getAnnotation(OperationType.class);
            if (operationType == null) {
                logger.warn(clazz.getName() + "接口没有OperationType注解");
            }

            //System.out.println(clazz.getSimpleName());
            //跳过匿名接口
            if (component instanceof IApiComponent && ((IApiComponent) component).supportAnonymousAccess().isSupportAnonymousAccess()) {
                return;
            }
            if (!Objects.equals(context.getId(), "framework") && !Objects.equals(context.getId(), "tenant")) {
                AuthAction authAction = clazz.getAnnotation(AuthAction.class);
                AuthActions authActions = clazz.getAnnotation(AuthActions.class);
                if (authAction == null && authActions == null) {
                    logger.warn(clazz.getName() + "接口没有AuthAction注解");
                }
            }
        }
    }

    @Override
    protected void myInit() {

    }

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
