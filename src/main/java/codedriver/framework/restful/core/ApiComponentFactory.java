package codedriver.framework.restful.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.google.common.util.concurrent.RateLimiter;

import codedriver.framework.common.RootComponent;
import codedriver.framework.restful.dto.ApiComponentVo;
import codedriver.framework.restful.dto.ApiVo;

@RootComponent
public class ApiComponentFactory implements ApplicationListener<ContextRefreshedEvent> {
	private static Map<String, ApiComponent> componentMap = new HashMap<>();
	private static List<ApiComponentVo> componentList = new ArrayList<>();
	private static List<ApiVo> apiList = new ArrayList<>();
	private static Map<String, ApiVo> apiMap = new HashMap<>();
	public static Map<String, RateLimiter> interfaceRateMap = new ConcurrentHashMap<>();
	private static Map<String, JsonStreamApiComponent> streamComponentMap = new HashMap<>();
	private static Map<String, BinaryStreamApiComponent> binaryComponentMap = new HashMap<>();

	public static ApiComponent getInstance(String componentId) {
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

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, ApiComponent> myMap = context.getBeansOfType(ApiComponent.class);
		Map<String, JsonStreamApiComponent> myStreamMap = context.getBeansOfType(JsonStreamApiComponent.class);
		Map<String, BinaryStreamApiComponent> myBinaryMap = context.getBeansOfType(BinaryStreamApiComponent.class);
		for (Map.Entry<String, ApiComponent> entry : myMap.entrySet()) {
			ApiComponent component = entry.getValue();
			if (component.getId() != null) {
				componentMap.put(component.getId(), component);
				ApiComponentVo restComponentVo = new ApiComponentVo();
				restComponentVo.setId(component.getId());
				restComponentVo.setName(component.getName());
				restComponentVo.setConfig(component.getConfig());
				restComponentVo.setModuleId(context.getId());
				componentList.add(restComponentVo);
				String token = component.getToken();
				if (StringUtils.isNotBlank(token)) {
					if (token.startsWith("/")) {
						token = token.substring(1);
					}
					if (token.endsWith("/")) {
						token = token.substring(0, token.length() - 1);
					}
					ApiVo restInterfaceVo = new ApiVo();
					restInterfaceVo.setAuthtype("token");
					restInterfaceVo.setToken(token);
					restInterfaceVo.setComponentId(component.getId());
					restInterfaceVo.setName(component.getName());
					restInterfaceVo.setIsActive(1);
					restInterfaceVo.setTimeout(0);// 0是default
					restInterfaceVo.setType(ApiVo.Type.OBJECT.getValue());
					restInterfaceVo.setModuleId(context.getId());
					if (!apiMap.containsKey(token)) {
						apiList.add(restInterfaceVo);
						apiMap.put(token, restInterfaceVo);
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
				ApiComponentVo restComponentVo = new ApiComponentVo();
				restComponentVo.setId(component.getId());
				restComponentVo.setName(component.getName());
				restComponentVo.setConfig(component.getConfig());
				restComponentVo.setModuleId(context.getId());
				componentList.add(restComponentVo);
				String token = component.getToken();
				if (StringUtils.isNotBlank(token)) {
					if (token.startsWith("/")) {
						token = token.substring(1);
					}
					if (token.endsWith("/")) {
						token = token.substring(0, token.length() - 1);
					}
					ApiVo restInterfaceVo = new ApiVo();
					restInterfaceVo.setAuthtype("");
					restInterfaceVo.setToken(token);
					restInterfaceVo.setComponentId(component.getId());
					restInterfaceVo.setExpire("");
					restInterfaceVo.setName(component.getName());
					restInterfaceVo.setIsActive(1);
					restInterfaceVo.setTimeout(0);// 0是default
					restInterfaceVo.setType(ApiVo.Type.STREAM.getValue());
					if (!apiMap.containsKey(token)) {
						apiList.add(restInterfaceVo);
						apiMap.put(token, restInterfaceVo);
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
				ApiComponentVo restComponentVo = new ApiComponentVo();
				restComponentVo.setId(component.getId());
				restComponentVo.setName(component.getName());
				restComponentVo.setConfig(component.getConfig());
				restComponentVo.setModuleId(context.getId());
				componentList.add(restComponentVo);
				String token = component.getToken();
				if (StringUtils.isNotBlank(token)) {
					if (token.startsWith("/")) {
						token = token.substring(1);
					}
					if (token.endsWith("/")) {
						token = token.substring(0, token.length() - 1);
					}
					ApiVo restInterfaceVo = new ApiVo();
					restInterfaceVo.setAuthtype("");
					restInterfaceVo.setToken(token);
					restInterfaceVo.setComponentId(component.getId());
					restInterfaceVo.setExpire("");
					restInterfaceVo.setName(component.getName());
					restInterfaceVo.setIsActive(1);
					restInterfaceVo.setTimeout(0);// 0是default
					restInterfaceVo.setType(ApiVo.Type.STREAM.getValue());
					if (!apiMap.containsKey(token)) {
						apiList.add(restInterfaceVo);
						apiMap.put(token, restInterfaceVo);
					} else {
						throw new RuntimeException("接口：" + token + "已存在，请重新定义访问路径");
					}
				}
			}
		}
	}
}
