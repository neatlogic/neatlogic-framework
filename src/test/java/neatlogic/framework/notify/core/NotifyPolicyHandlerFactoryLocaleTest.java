package neatlogic.framework.notify.core;

import neatlogic.framework.asynchronization.threadlocal.RequestContext;
import neatlogic.framework.notify.dto.NotifyTreeVo;
import neatlogic.framework.notify.dto.NotifyTriggerVo;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** 验证消息分类展示按请求语言重新读取，不修改启动缓存或公开注册范围。 */
public class NotifyPolicyHandlerFactoryLocaleTest {
    /** 连续切换语言时，模块、处理器和触发器名称均应独立刷新。 */
    @Test
    @SuppressWarnings("unchecked")
    public void shouldRefreshLocalizedTreeWithoutChangingCache() throws Exception {
        RequestContext requestContext = RequestContext.init((RequestContext) null);
        Field treeField = NotifyPolicyHandlerFactory.class.getDeclaredField("moduleTreeVoList");
        treeField.setAccessible(true);
        List<NotifyTreeVo> trees = (List<NotifyTreeVo>) treeField.get(null);
        List<NotifyTreeVo> originalTrees = new ArrayList<>(trees);
        Field handlerField = NotifyPolicyHandlerFactory.class.getDeclaredField("treeHandlerMap");
        handlerField.setAccessible(true);
        Map<String, INotifyPolicyHandler> handlers = (Map<String, INotifyPolicyHandler>) handlerField.get(null);
        Map<String, INotifyPolicyHandler> originalHandlers = new HashMap<>(handlers);
        try {
            INotifyPolicyHandler handler = (INotifyPolicyHandler) Proxy.newProxyInstance(
                    INotifyPolicyHandler.class.getClassLoader(), new Class<?>[]{INotifyPolicyHandler.class},
                    (proxy, method, args) -> {
                        boolean english = "en".equals(RequestContext.get().getLocale().getLanguage());
                        if ("getName".equals(method.getName())) return english ? "Task" : "工单";
                        if ("getNotifyTriggerList".equals(method.getName())) {
                            return List.of(new NotifyTriggerVo("start", english ? "Submit" : "上报", ""));
                        }
                        return null;
                    });
            NotifyTreeVo cached = new NotifyTreeVo("process", "test.notify.module");
            NotifyTreeVo child = new NotifyTreeVo("test.handler", "工单");
            child.addChildren(new NotifyTreeVo("start", "上报"));
            cached.addChildren(child);
            trees.clear();
            trees.add(cached);
            handlers.put("test.handler", handler);
            for (Locale locale : new Locale[]{Locale.ENGLISH, Locale.CHINESE, Locale.ENGLISH}) {
                requestContext.setLocale(locale);
                boolean english = Locale.ENGLISH.equals(locale);
                NotifyTreeVo result = NotifyPolicyHandlerFactory.getModuleTreeVoList().get(0);
                Assert.assertEquals(english ? "IT Service" : "IT服务", result.getName());
                Assert.assertEquals(english ? "Task" : "工单", result.getChildren().get(0).getName());
                Assert.assertEquals(english ? "Submit" : "上报", result.getChildren().get(0).getChildren().get(0).getName());
            }
            // 处理器返回的是当前语言展示文案，不应再次作为 key 查询并产生缺失诊断。
            Assert.assertEquals("test.notify.module", cached.getName());
            Assert.assertEquals("工单", child.getName());
            Assert.assertEquals("上报", child.getChildren().get(0).getName());
        } finally {
            trees.clear();
            trees.addAll(originalTrees);
            handlers.clear();
            handlers.putAll(originalHandlers);
            requestContext.release();
        }
    }
}
