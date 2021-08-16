/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.file.core;

import codedriver.framework.applicationlistener.core.ModuleInitializedListenerBase;
import codedriver.framework.bootstrap.CodedriverWebApplicationContext;
import codedriver.framework.common.RootComponent;

import java.util.HashMap;
import java.util.Map;

@RootComponent
public class FileStorageMediumFactory extends ModuleInitializedListenerBase {

    private static final Map<String, IFileStorageHandler> componentMap = new HashMap<>();

    public static IFileStorageHandler getHandler(String storageMedium) {
        storageMedium = storageMedium.toUpperCase();
        return componentMap.get(storageMedium);
    }


    @Override
    public void onInitialized(CodedriverWebApplicationContext context) {
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
