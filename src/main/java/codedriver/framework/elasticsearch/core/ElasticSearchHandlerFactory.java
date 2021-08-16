/*
 * Copyright(c) 2021 TechSure Co., Ltd. All Rights Reserved.
 * 本内容仅限于深圳市赞悦科技有限公司内部传阅，禁止外泄以及用于其他的商业项目。
 */

package codedriver.framework.elasticsearch.core;

import codedriver.framework.common.RootComponent;

@RootComponent
public class ElasticSearchHandlerFactory {//} extends ModuleInitializedListenerBase {
  /*  Logger logger = LoggerFactory.getLogger(ElasticSearchHandlerFactory.class);

    private static Map<String, IElasticSearchHandler> handlerMap = new HashMap<String, IElasticSearchHandler>();

    public static IElasticSearchHandler getHandler(String handler) {
        return handlerMap.get(handler);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext context = event.getApplicationContext();
        Map<String, IElasticSearchHandler> myMap = context.getBeansOfType(IElasticSearchHandler.class);
        for (Map.Entry<String, IElasticSearchHandler> entry : myMap.entrySet()) {
            try {
                IElasticSearchHandler handler = entry.getValue();
                handlerMap.put(handler.getDocument(), handler);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

    }

    @Override
    protected void myInit() {
        // TODO Auto-generated method stub

    }*/
}
