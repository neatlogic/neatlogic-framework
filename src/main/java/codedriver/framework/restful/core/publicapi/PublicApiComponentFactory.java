package codedriver.framework.restful.core.publicapi;

import codedriver.framework.asynchronization.thread.CodeDriverThread;
import codedriver.framework.asynchronization.threadlocal.TenantContext;
import codedriver.framework.asynchronization.threadpool.CachedThreadPool;
import codedriver.framework.common.RootComponent;
import codedriver.framework.dao.mapper.TenantMapper;
import codedriver.framework.dto.TenantVo;
import codedriver.framework.restful.core.IApiComponent;
import codedriver.framework.restful.core.IBinaryStreamApiComponent;
import codedriver.framework.restful.core.IJsonStreamApiComponent;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiHandlerVo;
import codedriver.framework.restful.dto.ApiVo;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RootComponent
public class PublicApiComponentFactory implements ApplicationListener<ContextRefreshedEvent> {
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
    private static final Map<String, ApiVo> apiTokenMap = new HashMap<>();
    // public static Map<String, RateLimiter> interfaceRateMap = new
    // ConcurrentHashMap<>();
    private static final Map<String, IJsonStreamApiComponent> streamComponentMap = new HashMap<>();
    private static final Map<String, IBinaryStreamApiComponent> binaryComponentMap = new HashMap<>();
    // 按照token表达式长度排序，最长匹配原则
    private static final Map<String, ApiVo> regexApiMap = new TreeMap<String, ApiVo>(new Comparator<String>() {
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

    public static Map<String, ApiVo> getApiTokenMap() {
        return apiTokenMap;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
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
                restComponentVo.setType(ApiVo.Type.OBJECT.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getClassName(), restComponentVo);
                initApiTokenList(JSONObject.parseObject(JSONObject.toJSONString(restComponentVo)),context.getId());
            }
        }

        for (Map.Entry<String, IPublicJsonStreamApiComponent> entry : myStreamMap.entrySet()) {
            IPublicJsonStreamApiComponent component = entry.getValue();
            if (component.getId() != null) {
                streamComponentMap.put(component.getId(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getId());
                restComponentVo.setToken(component.getToken());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(false);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiVo.Type.STREAM.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getId(), restComponentVo);
                initApiTokenList(JSONObject.parseObject(JSONObject.toJSONString(restComponentVo)),context.getId());
            }
        }

        for (Map.Entry<String, IPublicBinaryStreamApiComponent> entry : myBinaryMap.entrySet()) {
            IPublicBinaryStreamApiComponent component = entry.getValue();
            if (component.getId() != null) {
                binaryComponentMap.put(component.getId(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getId());
                restComponentVo.setToken(component.getToken());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(false);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiVo.Type.BINARY.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getId(), restComponentVo);
                initApiTokenList(JSONObject.parseObject(JSONObject.toJSONString(restComponentVo)),context.getId());
            }
        }

        //insert public token api
        List<TenantVo> tenantList = tenantMapper.getAllActiveTenant();
        for (TenantVo tenantVo : tenantList) {
            CachedThreadPool.execute(new InsertPublicTokenApiRunner(tenantVo.getUuid()));
        }
    }

    private static Pattern p = Pattern.compile("\\{([^}]+)\\}");

    /**
     * 初始化apiTokenList apiMap
     * @param componentJson component入参
     */
    private void initApiTokenList(JSONObject componentJson,String moduleId){
        String token = componentJson.getString("token");
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
            apiVo.setHandler(componentJson.getString("handler"));
            apiVo.setHandlerName(componentJson.getString("handler"));
            apiVo.setName(componentJson.getString("name"));
            apiVo.setIsActive(1);
            apiVo.setNeedAudit(0);
            apiVo.setTimeout(0);// 0是default
            apiVo.setType(componentJson.getString("type"));
            apiVo.setModuleId(moduleId);
            apiVo.setApiType(ApiVo.ApiType.CUSTOM.getValue());
            apiVo.setIsDeletable(0);// 不能删除
            apiVo.setIsPrivate(false);
            if (token.contains("{")) {
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
                    logger.error("路径匹配接口：" + regexToken + "  " + token + "已存在，请重新定义访问路径");
                    System.exit(1);
                }
            }
            if (!apiMap.containsKey(token)) {
                apiTokenList.add(apiVo);
                apiTokenMap.put(token, apiVo);
            } else {
                logger.error("接口：" + token + "已存在，请重新定义访问路径");
                System.exit(1);
            }
        }
    }

    /**
     *  线程类
     *  初始化
     */
    class InsertPublicTokenApiRunner extends CodeDriverThread {
        private final String tenantUuid;
        public InsertPublicTokenApiRunner(String tenantUuid){
            this.tenantUuid = tenantUuid;
        }

        @Override
        protected void execute() {
            Thread.currentThread().setName("PUBLIC-TOKEN-API-INIT-" + tenantUuid);
            // 切换租户数据源
            TenantContext.get().switchTenant(tenantUuid).setUseDefaultDatasource(false);
            for (ApiVo apiVo :apiTokenList){
               ApiVo api = apiMapper.getApiByToken(apiVo.getToken());
               if(api == null){
                   apiMapper.replaceApi(apiVo);
               }
            }
        }
    }
}
