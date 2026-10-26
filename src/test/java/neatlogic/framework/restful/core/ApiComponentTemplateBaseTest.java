package neatlogic.framework.restful.core;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.exception.core.ApiRuntimeException;
import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/** 独立保护公共模板的异常保真和回退语义，不依赖任何业务模块。 */
public class ApiComponentTemplateBaseTest {
    /** JDK 代理分支保留原业务异常全部信息，并且不重复执行本地回退。 */
    @Test
    public void proxyPreservesBusinessExceptionInstance() {
        Probe target = new Probe();
        DetailedFailure original = new DetailedFailure();
        original.setErrorCode("business-code");
        Exception cause = new Exception("original-cause");
        original.initCause(cause);
        target.failure = original;
        ApiRuntimeException actual = assertThrows(ApiRuntimeException.class, () -> proxy(target).invoke());
        assertSame(original, actual);
        assertSame(cause, actual.getCause());
        assertEquals("business-code", actual.getErrorCode());
        assertEquals("details", actual.getParam().getString("field"));
        assertEquals(1, target.serviceCalls);
        assertEquals(0, target.localCalls);
    }

    /** 没有当前代理时仍执行一次本地分支，原有反射解包责任不扩大。 */
    @Test
    public void absentProxyFallsBackOnce() {
        Probe target = new Probe();
        target.failure = new DetailedFailure();
        InvocationTargetException wrapper = assertThrows(InvocationTargetException.class, target::invoke);
        assertSame(target.failure, wrapper.getTargetException());
        assertEquals(1, target.localCalls);
        assertEquals(1, target.serviceCalls);
    }

    /** 未知异常保持原反射包装，业务异常 cause 不被递归遍历或重新分类。 */
    @Test
    public void unknownExceptionKeepsOriginalPropagation() {
        Probe target = new Probe();
        target.failure = new Exception("unknown", new DetailedFailure());
        InvocationTargetException wrapper = assertThrows(InvocationTargetException.class, () -> proxy(target).invoke());
        assertSame(target.failure, wrapper.getTargetException());
        assertEquals(0, target.localCalls);
        assertEquals(1, target.serviceCalls);
    }

    /** 使用实际 Spring 暴露代理，避免单纯构造异常掩盖代理路径回归。 */
    private Invocation proxy(Probe target) {
        ProxyFactory factory = new ProxyFactory(target);
        factory.setExposeProxy(true);
        factory.setProxyTargetClass(false);
        factory.addAdvice((MethodInterceptor) invocation -> invocation.proceed());
        return (Invocation) factory.getProxy();
    }

    /** 测试代理的额外入口，不修改生产接口。 */
    public interface Invocation {
        /** 调用真实公共模板。 */
        Object invoke() throws Exception;
    }

    /** 最小组件仅调用真实模板和反射辅助方法。 */
    public static class Probe extends ApiComponentBase implements Invocation {
        Exception failure;
        int localCalls;
        int serviceCalls;

        /** 保持代理与本地两条执行分支的真实异常形态。 */
        @Override public Object invoke() throws Exception {
            return invokeWithProxyFallback(
                    (proxy, targetClass) -> invokeComponentMethod(proxy, "myDoService", new Class[]{JSONObject.class}, new JSONObject()),
                    () -> {
                        localCalls++;
                        return invokeComponentMethod(this, "myDoService", new Class[]{JSONObject.class}, new JSONObject());
                    });
        }

        /** 记录实际执行次数，抛出指定原异常。 */
        @Override public Object myDoService(JSONObject data) throws Exception {
            serviceCalls++;
            throw failure;
        }

        /** 测试组件固定标识。 */
        @Override public String getName() { return "template-probe"; }
    }

    /** 模拟具有结构化详情的业务异常子类，不依赖翻译或容器初始化。 */
    private static class DetailedFailure extends ApiRuntimeException {
        /** 验证子类覆盖方法在经过模板后仍然生效。 */
        @Override public JSONObject getParam() {
            JSONObject param = new JSONObject();
            param.put("field", "details");
            return param;
        }
    }
}
