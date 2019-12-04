package codedriver.framework.restful.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.google.common.util.concurrent.RateLimiter;

import codedriver.framework.common.RootComponent;
import codedriver.framework.common.util.ModuleUtil;
import codedriver.framework.dao.mapper.ModuleMapper;
import codedriver.framework.dto.ModuleVo;
import codedriver.framework.restful.dao.mapper.ApiMapper;
import codedriver.framework.restful.dto.ApiComponentVo;
import codedriver.framework.restful.dto.ApiVo;

@RootComponent
public class ApiComponentFactory implements ApplicationListener<ContextRefreshedEvent> {
	private static Map<String, ApiComponent> componentMap = new HashMap<>();

	public static Map<String, RateLimiter> interfaceRateMap = new ConcurrentHashMap<>();

	private static Map<String, JsonStreamApiComponent> streamComponentMap = new HashMap<>();

	public static ApiComponent getInstance(String stepType) {
		return componentMap.get(stepType);
	}

	public static JsonStreamApiComponent getStreamInstance(String stepType) {
		return streamComponentMap.get(stepType);
	}

	@PostConstruct
	public final void init() {
		apiMapper.deleteAllApiComponent();
	}

	@Autowired
	private ApiMapper apiMapper;

	@Autowired
	private ModuleMapper moduleMapper;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, ApiComponent> myMap = context.getBeansOfType(ApiComponent.class);
		Map<String, JsonStreamApiComponent> myStreamMap = context.getBeansOfType(JsonStreamApiComponent.class);
		List<ModuleVo> moduleList = ModuleUtil.getAllModuleList();
		String module = "";
		for (ModuleVo vo : moduleList) {
			if (context.getId().equals(vo.getId())) {
				module = vo.getId();
			}
		}
		for (Map.Entry<String, ApiComponent> entry : myMap.entrySet()) {
			ApiComponent component = entry.getValue();
			if (component.getId() != null) {
				componentMap.put(component.getId(), component);
				ApiComponentVo restComponentVo = new ApiComponentVo();
				restComponentVo.setId(component.getId());
				restComponentVo.setName(component.getName());
				restComponentVo.setConfig(component.getConfig());
				restComponentVo.setModule(module);
				apiMapper.replaceApiComponent(restComponentVo);
				// restMapper.activeRestInterfaceByComponentId(component.getId());
				String token = component.getToken();
				if (StringUtils.isNotBlank(token)) {
					if (token.startsWith("/")) {
						token = token.substring(1);
					}
					if (token.endsWith("/")) {
						token = token.substring(0, token.length() - 1);
					}
					ApiVo restInterfaceVo = apiMapper.getApiByToken(token);
					if (restInterfaceVo == null) {
						restInterfaceVo = new ApiVo();
						restInterfaceVo.setAuthtype("token");
						restInterfaceVo.setToken(token);
						restInterfaceVo.setComponentId(component.getId());
						restInterfaceVo.setExpire("");
						restInterfaceVo.setName(component.getName());
						restInterfaceVo.setIsActive(1);
						restInterfaceVo.setTimeout(0);// 0是default
						restInterfaceVo.setType(ApiVo.Type.OBJECT.getValue());
						apiMapper.insertApi(restInterfaceVo);
					} else {
						// 如果restInterface的compoennt_id和restComponent的id对应不上，则需要更新restInterface的component_id
						String oldComponentId = restInterfaceVo.getComponentId();
						String newComponentId = component.getId();
						if (!component.getId().equals(oldComponentId)) {
							List<ApiVo> restInterfaceList = apiMapper.getApiByComponentId(oldComponentId);
							for (ApiVo vo : restInterfaceList) {
								vo.setComponentId(newComponentId);
								apiMapper.updateApiComponentIdById(vo);
							}
						}
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
				restComponentVo.setModule(module);
				apiMapper.replaceApiComponent(restComponentVo);
				// restMapper.activeRestInterfaceByComponentId(component.getId());
				String token = component.getToken();
				if (StringUtils.isNotBlank(token)) {
					if (token.startsWith("/")) {
						token = token.substring(1);
					}
					if (token.endsWith("/")) {
						token = token.substring(0, token.length() - 1);
					}
					ApiVo restInterfaceVo = apiMapper.getApiByToken(token);
					if (restInterfaceVo == null) {
						restInterfaceVo = new ApiVo();
						restInterfaceVo.setAuthtype("");
						restInterfaceVo.setToken(token);
						restInterfaceVo.setComponentId(component.getId());
						restInterfaceVo.setExpire("");
						restInterfaceVo.setName(component.getName());
						restInterfaceVo.setIsActive(1);
						restInterfaceVo.setTimeout(0);// 0是default
						restInterfaceVo.setType(ApiVo.Type.STREAM.getValue());
						apiMapper.insertApi(restInterfaceVo);
					} else {
						// 如果restInterface的compoennt_id和restComponent的id对应不上，则需要更新restInterface的component_id
						String oldComponentId = restInterfaceVo.getComponentId();
						String newComponentId = component.getId();
						if (!component.getId().equals(oldComponentId)) {
							List<ApiVo> restInterfaceList = apiMapper.getApiByComponentId(oldComponentId);
							for (ApiVo vo : restInterfaceList) {
								vo.setComponentId(newComponentId);
								apiMapper.updateApiComponentIdById(vo);
							}
						}
					}
				}
			}
		}
	}
}
