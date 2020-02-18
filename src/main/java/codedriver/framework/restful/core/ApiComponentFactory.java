package codedriver.framework.restful.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.ClassUtils;

import com.google.common.util.concurrent.RateLimiter;

import codedriver.framework.common.RootComponent;
import codedriver.framework.restful.annotation.IsActive;
import codedriver.framework.restful.dto.ApiHandlerVo;
import codedriver.framework.restful.dto.ApiVo;

@RootComponent
public class ApiComponentFactory implements ApplicationListener<ContextRefreshedEvent> {

	private static Map<String, IApiComponent> componentMap = new HashMap<>();
	private static List<ApiHandlerVo> apiHandlerList = new ArrayList<>();
	private static Map<String, ApiHandlerVo> apiHandlerMap = new HashMap<>();
	private static List<ApiVo> apiList = new ArrayList<>();
	private static Map<String, ApiVo> apiMap = new HashMap<>();
	public static Map<String, RateLimiter> interfaceRateMap = new ConcurrentHashMap<>();
	private static Map<String, JsonStreamApiComponent> streamComponentMap = new HashMap<>();
	private static Map<String, BinaryStreamApiComponent> binaryComponentMap = new HashMap<>();

	public static IApiComponent getInstance(String componentId) {
		return componentMap.get(componentId);
	}

	public static JsonStreamApiComponent getStreamInstance(String componentId) {
		return streamComponentMap.get(componentId);
	}

	public static BinaryStreamApiComponent getBinaryInstance(String componentId) {
		return binaryComponentMap.get(componentId);
	}

	public static ApiVo getApiByToken(String token) {
		return apiMap.get(token);
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

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IApiComponent> myMap = context.getBeansOfType(IApiComponent.class);
		Map<String, JsonStreamApiComponent> myStreamMap = context.getBeansOfType(JsonStreamApiComponent.class);
		Map<String, BinaryStreamApiComponent> myBinaryMap = context.getBeansOfType(BinaryStreamApiComponent.class);
		for (Map.Entry<String, IApiComponent> entry : myMap.entrySet()) {
			IApiComponent component = entry.getValue();
			if (component.getClassName() != null) {
				componentMap.put(component.getClassName(), component);
				ApiHandlerVo restComponentVo = new ApiHandlerVo();
				restComponentVo.setHandler(component.getClassName());
				restComponentVo.setName(component.getName());
				restComponentVo.setConfig(component.getConfig());
				restComponentVo.setPrivate(component.isPrivate());
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
					Class<?> targetClass = AopUtils.getTargetClass(component);
					if (targetClass.getAnnotation(IsActive.class) != null) {
						apiVo.setIsActive(1);
					} else {
						apiVo.setIsActive(0);
					}
					apiVo.setNeedAudit(component.needAudit());
					apiVo.setTimeout(0);// 0是default
					apiVo.setType(ApiVo.Type.OBJECT.getValue());
					apiVo.setModuleId(context.getId());
					apiVo.setIsDeletable(0);// 不能删除
					if (!apiMap.containsKey(token)) {
						apiList.add(apiVo);
						apiMap.put(token, apiVo);
					} else {
						throw new RuntimeException("接口：" + token + "已存在，请重新定义访问路径");
					}
				}
			}
		}

		for (Map.Entry<String, JsonStreamApiComponent> entry : myStreamMap.entrySet()) {
			JsonStreamApiComponent component = entry.getValue();
			if (component.getId() != null) {
				streamComponentMap.put(component.getId(), component);
				ApiHandlerVo restComponentVo = new ApiHandlerVo();
				restComponentVo.setHandler(component.getId());
				restComponentVo.setName(component.getName());
				restComponentVo.setConfig(component.getConfig());
				restComponentVo.setPrivate(component.isPrivate());
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
					apiVo.setAuthtype("");
					apiVo.setToken(token);
					apiVo.setHandler(component.getId());
					apiVo.setExpire("");
					apiVo.setHandlerName(component.getName());
					apiVo.setName(component.getName());
					Class<?> targetClass = AopUtils.getTargetClass(component);
					if (targetClass.getAnnotation(IsActive.class) != null) {
						apiVo.setIsActive(1);
					} else {
						apiVo.setIsActive(0);
					}
					apiVo.setNeedAudit(component.needAudit());
					apiVo.setTimeout(0);// 0是default
					apiVo.setType(ApiVo.Type.STREAM.getValue());
					apiVo.setIsDeletable(0);// 不能删除
					if (!apiMap.containsKey(token)) {
						apiList.add(apiVo);
						apiMap.put(token, apiVo);
					} else {
						throw new RuntimeException("接口：" + token + "已存在，请重新定义访问路径");
					}
				}
			}
		}

		for (Map.Entry<String, BinaryStreamApiComponent> entry : myBinaryMap.entrySet()) {
			BinaryStreamApiComponent component = entry.getValue();
			if (component.getId() != null) {
				binaryComponentMap.put(component.getId(), component);
				ApiHandlerVo restComponentVo = new ApiHandlerVo();
				restComponentVo.setHandler(component.getId());
				restComponentVo.setName(component.getName());
				restComponentVo.setConfig(component.getConfig());
				restComponentVo.setPrivate(component.isPrivate());
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
					apiVo.setAuthtype("");
					apiVo.setToken(token);
					apiVo.setHandler(component.getId());
					apiVo.setExpire("");
					apiVo.setHandlerName(component.getName());
					apiVo.setName(component.getName());
					Class<?> targetClass = AopUtils.getTargetClass(component);
					if (targetClass.getAnnotation(IsActive.class) != null) {
						apiVo.setIsActive(1);
					} else {
						apiVo.setIsActive(0);
					}
					apiVo.setNeedAudit(component.needAudit());
					apiVo.setTimeout(0);// 0是default
					apiVo.setType(ApiVo.Type.BINARY.getValue());
					apiVo.setIsDeletable(0);// 不能删除
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
}
