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

package neatlogic.framework.restful.core.publicapi;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.applicationlistener.core.ModuleInitializedListenerBase;
import neatlogic.framework.asynchronization.thread.NeatLogicThread;
import neatlogic.framework.asynchronization.threadlocal.TenantContext;
import neatlogic.framework.asynchronization.threadpool.CachedThreadPool;
import neatlogic.framework.bootstrap.NeatLogicWebApplicationContext;
import neatlogic.framework.common.RootComponent;
import neatlogic.framework.common.config.Config;
import neatlogic.framework.dao.mapper.TenantMapper;
import neatlogic.framework.dto.TenantVo;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.IBinaryStreamApiComponent;
import neatlogic.framework.restful.core.IJsonStreamApiComponent;
import neatlogic.framework.restful.dao.mapper.ApiMapper;
import neatlogic.framework.restful.dto.ApiHandlerVo;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiKind;
import neatlogic.framework.restful.enums.ApiType;
import neatlogic.framework.restful.enums.PublicApiAuthType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RootComponent
public class PublicApiComponentFactory extends ModuleInitializedListenerBase {
    static Logger logger = LoggerFactory.getLogger(PublicApiComponentFactory.class);
    @Resource
    private TenantMapper tenantMapper;

    @Resource
    private ApiMapper apiMapper;

    private static final Map<String, IApiComponent> componentMap = new HashMap<>();
    private static final List<ApiHandlerVo> apiHandlerList = new ArrayList<>();
    private static final Map<String, ApiHandlerVo> apiHandlerMap = new HashMap<>();
    private static final List<ApiVo> apiList = new ArrayList<>();
    private static final List<ApiVo> apiTokenList = new ArrayList<>();
    private static final Map<String, ApiVo> apiMap = new HashMap<>();
    // public static Map<String, RateLimiter> interfaceRateMap = new
    // ConcurrentHashMap<>();
    private static final Map<String, IJsonStreamApiComponent> streamComponentMap = new HashMap<>();
    private static final Map<String, IBinaryStreamApiComponent> binaryComponentMap = new HashMap<>();
    // 按照token表达式长度排序，最长匹配原则
    private static final Map<String, ApiVo> regexApiMap = new TreeMap<>((o1, o2) -> {
        // 先按照长度排序，如果长度一样按照内容排序
        if (o1.length() != o2.length()) {
            return o1.length() - o2.length();
        } else {
            return o1.compareTo(o2);
        }
    });

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

    public static List<ApiVo> getApiTokenList() {
        return apiTokenList;
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


    @Override
    protected void onInitialized(NeatLogicWebApplicationContext context) {
        Map<String, IPublicApiComponent> myMap = context.getBeansOfType(IPublicApiComponent.class);
        Map<String, IPublicJsonStreamApiComponent> myStreamMap = context.getBeansOfType(IPublicJsonStreamApiComponent.class);
        Map<String, IPublicBinaryStreamApiComponent> myBinaryMap = context.getBeansOfType(IPublicBinaryStreamApiComponent.class);
        for (Map.Entry<String, IPublicApiComponent> entry : myMap.entrySet()) {
            IPublicApiComponent component = entry.getValue();
            if (component.getClassName() != null) {
                componentMap.put(component.getClassName(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getClassName());
                restComponentVo.setToken(component.getToken());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(false);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiType.OBJECT.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getClassName(), restComponentVo);
                initApiTokenList(JSONObject.parseObject(JSONObject.toJSONString(restComponentVo)), context.getId());
            }
        }

        for (Map.Entry<String, IPublicJsonStreamApiComponent> entry : myStreamMap.entrySet()) {
            IPublicJsonStreamApiComponent component = entry.getValue();
            if (component.getClassName() != null) {
                streamComponentMap.put(component.getClassName(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getClassName());
                restComponentVo.setToken(component.getToken());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(false);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiType.STREAM.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getClassName(), restComponentVo);
                initApiTokenList(JSONObject.parseObject(JSONObject.toJSONString(restComponentVo)), context.getId());
            }
        }

        for (Map.Entry<String, IPublicBinaryStreamApiComponent> entry : myBinaryMap.entrySet()) {
            IPublicBinaryStreamApiComponent component = entry.getValue();
            if (component.getClassName() != null) {
                binaryComponentMap.put(component.getClassName(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getClassName());
                restComponentVo.setToken(component.getToken());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(false);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiType.BINARY.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getClassName(), restComponentVo);
                initApiTokenList(JSONObject.parseObject(JSONObject.toJSONString(restComponentVo)), context.getId());
            }
        }

        //insert public token api
        List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();
        for (TenantVo tenantVo : tenantList) {
            CachedThreadPool.execute(new InsertPublicTokenApiRunner(tenantVo.getUuid()));
        }
    }

    @Override
    protected void myInit() {

    }

    private static final Pattern p = Pattern.compile("\\{([^}]+)\\}");

    /**
     * 初始化apiTokenList apiMap
     *
     * @param componentJson component入参
     */
    private void initApiTokenList(JSONObject componentJson, String moduleId) {
        String token = componentJson.getString("token");
        if (StringUtils.isNotBlank(token)) {
            if (token.startsWith("/")) {
                token = token.substring(1);
            }
            if (token.endsWith("/")) {
                token = token.substring(0, token.length() - 1);
            }
            ApiVo apiVo = new ApiVo();
            apiVo.setAuthtype(PublicApiAuthType.BASIC.getValue());
            apiVo.setUsername(Config.PUBLIC_API_AUTH_USERNAME());
            apiVo.setPassword(Config.PUBLIC_API_AUTH_PASSWORD());
            apiVo.setToken(token);
            apiVo.setHandler(componentJson.getString("handler"));
            apiVo.setHandlerName(componentJson.getString("handler"));
            apiVo.setName(componentJson.getString("name"));
            apiVo.setIsActive(1);
            apiVo.setNeedAudit(0);
            apiVo.setTimeout(0);// 0是default
            apiVo.setType(componentJson.getString("type"));
            apiVo.setModuleId(moduleId);
            apiVo.setApiType(ApiKind.CUSTOM.getValue());
            apiVo.setIsDeletable(0);// 不能删除
            apiVo.setIsPrivate(false);
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
                    logger.error("路径匹配接口：" + regexToken + "  " + token + "已存在，请重新定义访问路径");
                    System.exit(1);
                }
            }
            if (!apiMap.containsKey(token)) {
                apiTokenList.add(apiVo);
                apiMap.put(token, apiVo);
            } else {
                logger.error("接口：" + token + "已存在，请重新定义访问路径");
                System.exit(1);
            }
        }
    }

    /**
     * 线程类
     * 初始化
     */
    class InsertPublicTokenApiRunner extends NeatLogicThread {
        private final String tenantUuid;

        public InsertPublicTokenApiRunner(String tenantUuid) {
            super("PUBLIC-TOKEN-API-INIT-" + tenantUuid);
            this.tenantUuid = tenantUuid;
        }

        @Override
        protected void execute() {
            // 切换租户数据源
            TenantContext.get().switchTenant(tenantUuid).setUseDefaultDatasource(false);
            for (ApiVo apiVo : apiTokenList) {
                //ApiVo api = apiMapper.getApiByToken(apiVo.getToken());
                //if (api == null) {
                //即使接口存在也可能需要更新模块、类型、描述等信息
                apiMapper.insertApi(apiVo);
                // }
            }
        }
    }
}
