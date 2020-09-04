package codedriver.framework.restful.core.publicapi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import codedriver.framework.common.RootComponent;
import codedriver.framework.restful.core.IApiComponent;
import codedriver.framework.restful.core.IBinaryStreamApiComponent;
import codedriver.framework.restful.core.IJsonStreamApiComponent;
import codedriver.framework.restful.dto.ApiHandlerVo;
import codedriver.framework.restful.dto.ApiVo;

@RootComponent
public class PublicApiComponentFactory implements ApplicationListener<ContextRefreshedEvent> {
    static Logger logger = LoggerFactory.getLogger(PublicApiComponentFactory.class);

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
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(false);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiVo.Type.OBJECT.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getClassName(), restComponentVo);
            }
        }

        for (Map.Entry<String, IPublicJsonStreamApiComponent> entry : myStreamMap.entrySet()) {
            IPublicJsonStreamApiComponent component = entry.getValue();
            if (component.getId() != null) {
                streamComponentMap.put(component.getId(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getId());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(false);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiVo.Type.STREAM.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getId(), restComponentVo);
            }
        }

        for (Map.Entry<String, IPublicBinaryStreamApiComponent> entry : myBinaryMap.entrySet()) {
            IPublicBinaryStreamApiComponent component = entry.getValue();
            if (component.getId() != null) {
                binaryComponentMap.put(component.getId(), component);
                ApiHandlerVo restComponentVo = new ApiHandlerVo();
                restComponentVo.setHandler(component.getId());
                restComponentVo.setName(component.getName());
                restComponentVo.setConfig(component.getConfig());
                restComponentVo.setPrivate(false);
                restComponentVo.setModuleId(context.getId());
                restComponentVo.setType(ApiVo.Type.BINARY.getValue());
                apiHandlerList.add(restComponentVo);
                apiHandlerMap.put(component.getId(), restComponentVo);
            }
        }
    }
}
