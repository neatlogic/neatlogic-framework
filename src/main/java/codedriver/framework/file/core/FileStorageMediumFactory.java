package codedriver.framework.file.core;

import codedriver.framework.applicationlistener.core.ApplicationListenerBase;
import codedriver.framework.common.RootComponent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class FileStorageMediumFactory extends ApplicationListenerBase {

    private static final Map<String, IFileStorageHandler> componentMap = new HashMap<>();

    public static IFileStorageHandler getHandler(String storageMedium) {
        storageMedium = storageMedium.toUpperCase();
        return componentMap.get(storageMedium);
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IFileStorageHandler> myMap = context.getBeansOfType(IFileStorageHandler.class);
        for (Map.Entry<String, IFileStorageHandler> entry : myMap.entrySet()) {
            IFileStorageHandler fileStorageMediumHandler = entry.getValue();
            componentMap.put(fileStorageMediumHandler.getName().toUpperCase(), fileStorageMediumHandler);
        }
    }

	@Override
	protected void myInit() {
		// TODO Auto-generated method stub
		
	}
}
