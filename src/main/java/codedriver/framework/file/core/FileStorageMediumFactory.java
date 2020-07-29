package codedriver.framework.file.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class FileStorageMediumFactory extends ApplicationListenerBase {

	private static final Map<String, IFileStorageMediumHandler> componentMap = new HashMap<>();

	public static IFileStorageMediumHandler getHandler(String storageMedium) {
		storageMedium = storageMedium.toUpperCase();
		return componentMap.get(storageMedium);
	}


	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext context = event.getApplicationContext();
		Map<String, IFileStorageMediumHandler> myMap = context.getBeansOfType(IFileStorageMediumHandler.class);
		for (Map.Entry<String, IFileStorageMediumHandler> entry : myMap.entrySet()) {
			IFileStorageMediumHandler fileStorageMediumHandler = entry.getValue();
			componentMap.put(fileStorageMediumHandler.getName().toUpperCase(), fileStorageMediumHandler);
		}
	}

	@Override
	protected void myInit() {
		// TODO Auto-generated method stub
		
	}
}
