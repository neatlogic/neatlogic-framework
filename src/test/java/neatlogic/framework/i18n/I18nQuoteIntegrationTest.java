package neatlogic.framework.i18n;

import neatlogic.framework.asynchronization.threadlocal.UserContext;
import neatlogic.framework.exception.type.PermissionDeniedException;
import neatlogic.framework.util.I18nUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

/** 使用隔离语言资源验证 Spring 缓存和静态翻译入口的实际行为。 */
public class I18nQuoteIntegrationTest {
    private static final Locale TEST_LOCALE = new Locale("en", "XQ");
    private Locale previousLocale;
    private UserContext previousUserContext;
    private ModuleJsonMessageSource source;

    /** 使用专用模块资源，避免测试文案与生产 key 冲突。 */
    @Before
    public void setUp() {
        previousLocale = Locale.getDefault();
        previousUserContext = UserContext.get() == null ? null : UserContext.get().copy();
        if (UserContext.get() != null) {
            UserContext.get().release();
        }
        source = new ModuleJsonMessageSource();
    }

    /** 恢复测试前的默认语言，避免其他测试修改造成相互影响。 */
    @After
    public void tearDown() {
        Locale.setDefault(previousLocale);
        if (UserContext.get() != null) {
            UserContext.get().release();
        }
        if (previousUserContext != null) {
            UserContext.init(previousUserContext);
        }
    }

    /** 两个入口应输出相同文案，且连续调用时参数不得残留。 */
    @Test
    public void formatsBothEntrypointsAndReusesCache() {
        String[] keys = {"quoted", "escaped", "apostrophe"};
        String[] expected = {"Model 'Server' does not exist", "Model 'Server' does not exist", "Can't find Server"};
        for (int i = 0; i < keys.length; i++) {
            assertEquals(expected[i], source.getMessage(keys[i], new Object[]{"Server"}, TEST_LOCALE));
            assertEquals(expected[i], I18nUtils.getStaticMessage(TEST_LOCALE, keys[i], "Server"));
        }
        assertEquals("Model 'Switch' does not exist", source.getMessage("quoted", new Object[]{"Switch"}, TEST_LOCALE));
        String argument = "O'Brien {0} $1 \\path";
        String output = "Model '" + argument + "' does not exist";
        assertEquals(output, source.getMessage("quoted", new Object[]{argument}, TEST_LOCALE));
        assertEquals(output, I18nUtils.getStaticMessage(TEST_LOCALE, "quoted", argument));
    }

    /** 截图中的四个位置必须分别使用对应参数，不能残留占位符。 */
    @Test
    public void formatsScreenshotMessage() {
        Object[] args = {"Server", "server", "IP Address", "ip"};
        String expected = "The value of the unique rule attribute 'IP Address' (ip) of model 'Server (server)' is empty";
        assertEquals(expected, source.getMessage("screenshot", args, TEST_LOCALE));
        assertEquals(expected, I18nUtils.getStaticMessage(TEST_LOCALE, "screenshot", args));
    }

    /** 存量异常直接使用中文模板时也必须替换权限名称。 */
    @Test
    public void formatsLegacyPermissionDeniedMessage() {
        UserContext userContext = UserContext.init((UserContext) null);
        userContext.setUserName("管理员");
        userContext.setUserId("admin");
        PermissionDeniedException exception = new PermissionDeniedException(List.of("基线巡检查看权限"));
        assertEquals("当前用户“管理员(admin)”缺少“基线巡检查看权限”权限，请联系管理员", exception.getMessage());
        assertEquals("当前用户“管理员(admin)”没有权限执行该操作，请联系管理员",
                new PermissionDeniedException().getMessage());
    }

    /** 当前用户信息不完整时使用可用字段，不存在时不展示用户身份。 */
    @Test
    public void formatsIncompleteAndMissingUsers() {
        UserContext userContext = UserContext.init((UserContext) null);
        userContext.setUserName("管理员");
        assertEquals("当前用户“管理员”没有权限执行该操作，请联系管理员",
                new PermissionDeniedException().getMessage());

        userContext.setUserName(null);
        userContext.setUserId("admin");
        assertEquals("当前用户“admin”没有权限执行该操作，请联系管理员",
                new PermissionDeniedException().getMessage());

        userContext.release();
        assertEquals("当前用户缺少“基线巡检查看权限”权限，请联系管理员",
                new PermissionDeniedException(List.of("基线巡检查看权限")).getMessage());
        assertEquals("没有权限执行该操作，请联系管理员", new PermissionDeniedException().getMessage());
    }

    /** Spring 无参数路径仍返回原文，缺失 key 按存量消息模板兼容格式化。 */
    @Test
    public void preservesRawMessagesAndMissingKeys() {
        source.getMessage("quoted", new Object[]{"Server"}, TEST_LOCALE);
        assertEquals("Model '{0}' does not exist", source.getMessage("quoted", null, TEST_LOCALE));
        assertEquals("Can't find anything", source.getMessage("plain", new Object[0], TEST_LOCALE));
        assertEquals("Can't find anything", I18nUtils.getStaticMessage(TEST_LOCALE, "plain"));
        assertEquals("missing", I18nUtils.getStaticMessage(TEST_LOCALE, "missing", "Server"));
        assertEquals("missing {0}", I18nUtils.getStaticMessage(TEST_LOCALE, "missing {0}"));
        assertEquals("fallback", source.getMessage("missing", new Object[]{"Server"}, "fallback", TEST_LOCALE));
        try {
            source.getMessage("missing '{0}'", new Object[]{"Server"}, TEST_LOCALE);
            fail("Spring 消息源缺少 key 时应保持标准 NoSuchMessageException 语义");
        } catch (NoSuchMessageException ignored) {
            // 业务入口 I18nUtils 负责格式化存量文案，底层消息源保持 Spring 标准行为。
        }
        assertEquals("missing 'Server'", I18nUtils.getStaticMessage(TEST_LOCALE, "missing '{0}'", "Server"));
        assertEquals("当前用户缺少“基线巡检查看权限”权限，请联系管理员",
                I18nUtils.getStaticMessage(TEST_LOCALE, "当前用户缺少“{0}”权限，请联系{1}",
                        "基线巡检查看权限", "管理员"));
    }

    /** 高级格式继续采用原生语义，不将被引用的占位符转换为参数。 */
    @Test
    public void preservesAdvancedFormat() {
        Object[] args = {"Server", 1234};
        String expected = new MessageFormat("'{0}' {1,number,integer}", TEST_LOCALE).format(args);
        assertEquals(expected, source.getMessage("complex", args, TEST_LOCALE));
        assertEquals(expected, I18nUtils.getStaticMessage(TEST_LOCALE, "complex", args));
    }

    /** Spring 消息参数中的嵌套消息应先解析，且不得修改调用方数组。 */
    @Test
    public void resolvesNestedMessageArgumentsWithoutMutatingCallerArray() {
        DefaultMessageSourceResolvable nested = new DefaultMessageSourceResolvable(new String[]{"plain"});
        Object[] arguments = {nested};
        assertEquals("Model 'Can't find anything' does not exist",
                source.getMessage("quoted", arguments, TEST_LOCALE));
        assertSame(nested, arguments[0]);
    }

    /** 不支持的语言整体回退中文，且静态入口不得修改 JVM 默认 Locale。 */
    @Test
    public void fallsBackToChineseWithoutChangingDefaultLocale() {
        Locale defaultLocale = Locale.getDefault();
        assertEquals("中文回退", I18nUtils.getStaticMessage(Locale.FRENCH, "fallback"));
        assertEquals(defaultLocale, Locale.getDefault());
    }

    /** 并发格式化必须隔离参数，不能共享 MessageFormat 的可变状态。 */
    @Test
    public void formatsConcurrentlyWithoutArgumentLeakage() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        try {
            List<Callable<String>> tasks = new ArrayList<>();
            for (int index = 0; index < 200; index++) {
                String argument = "Server-" + index;
                tasks.add(() -> source.getMessage("quoted", new Object[]{argument}, TEST_LOCALE));
            }
            List<Future<String>> futures = executor.invokeAll(tasks);
            for (int index = 0; index < futures.size(); index++) {
                assertEquals("Model 'Server-" + index + "' does not exist", futures.get(index).get());
            }
        } finally {
            executor.shutdownNow();
        }
    }

}
