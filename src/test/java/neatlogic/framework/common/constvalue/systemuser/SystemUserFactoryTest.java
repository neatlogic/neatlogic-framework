package neatlogic.framework.common.constvalue.systemuser;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 验证系统用户列表读取不会修改注册信息、生成凭据或遗漏后续注册的用户。
 */
public class SystemUserFactoryTest {
    private Map<String, ISystemUser> systemUserMap;
    private Map<String, ISystemUser> originalSystemUserMap;

    /**
     * 保存工厂注册信息，使用反射替换测试数据，不增加全局扫描可发现的实现类。
     */
    @Before
    @SuppressWarnings("unchecked")
    public void prepareRegistry() throws Exception {
        Field field = SystemUserFactory.class.getDeclaredField("systemUserMap");
        field.setAccessible(true);
        systemUserMap = (Map<String, ISystemUser>) field.get(null);
        originalSystemUserMap = new HashMap<>(systemUserMap);
    }

    /**
     * 恢复原始注册信息，避免测试用户污染同一进程中的其他用例。
     */
    @After
    public void restoreRegistry() {
        if (originalSystemUserMap != null) {
            systemUserMap.clear();
            systemUserMap.putAll(originalSystemUserMap);
        }
    }

    /**
     * 调用方可调整列表内容，但不能通过该列表删除工厂注册用户。
     */
    @Test
    public void shouldReturnIndependentSnapshot() {
        ISystemUser extensionUser = createSystemUser("extension-id", "extension-uuid");
        systemUserMap.put(extensionUser.getUserId(), extensionUser);

        List<ISystemUser> snapshot = SystemUserFactory.getSystemUserList();
        Assert.assertTrue(snapshot.contains(extensionUser));
        snapshot.clear();

        Assert.assertSame(extensionUser, SystemUserFactory.getSystemUserByUserId("extension-id"));
        Assert.assertTrue(SystemUserFactory.getSystemUserList().contains(extensionUser));
    }

    /**
     * 新注册用户出现在下一次读取中，已有快照保持不变，读取过程不能访问凭据。
     */
    @Test
    public void shouldIncludeNewRegisteredUsersWithoutChangingEarlierSnapshot() {
        List<ISystemUser> earlierSnapshot = SystemUserFactory.getSystemUserList();
        ISystemUser extensionUser = createSystemUser("extension-id", "extension-uuid");
        systemUserMap.put(extensionUser.getUserId(), extensionUser);

        List<ISystemUser> latestSnapshot = SystemUserFactory.getSystemUserList();

        Assert.assertFalse(earlierSnapshot.contains(extensionUser));
        Assert.assertTrue(latestSnapshot.contains(extensionUser));
        Assert.assertEquals(earlierSnapshot.size() + 1, latestSnapshot.size());
        for (ISystemUser originalUser : originalSystemUserMap.values()) {
            Assert.assertTrue(latestSnapshot.contains(originalUser));
        }
    }

    /**
     * 列表顺序只由用户 ID、UUID 决定，不依赖注册表键名或注册顺序。
     */
    @Test
    public void shouldSortByUserIdThenUuid() {
        ISystemUser lastUser = createSystemUser("z-user", "a-uuid");
        ISystemUser secondUser = createSystemUser("a-user", "z-uuid");
        ISystemUser firstUser = createSystemUser("a-user", "a-uuid");
        systemUserMap.clear();
        // 测试相同用户 ID 的 UUID 次级排序时，用独立注册键保留两个定义。
        systemUserMap.put("first-key", lastUser);
        systemUserMap.put("second-key", secondUser);
        systemUserMap.put("last-key", firstUser);

        Assert.assertEquals(Arrays.asList(firstUser, secondUser, lastUser), SystemUserFactory.getSystemUserList());
        Assert.assertEquals(Arrays.asList(firstUser, secondUser, lastUser), SystemUserFactory.getSystemUserList());
    }

    /**
     * 创建仅允许读取排序标识的动态替身，若列表查询触发凭据构造则立即失败。
     */
    private ISystemUser createSystemUser(String userId, String userUuid) {
        return (ISystemUser) Proxy.newProxyInstance(
                ISystemUser.class.getClassLoader(),
                new Class[]{ISystemUser.class},
                (proxy, method, args) -> {
                    if ("getUserId".equals(method.getName())) {
                        return userId;
                    }
                    if ("getUserUuid".equals(method.getName())) {
                        return userUuid;
                    }
                    if ("equals".equals(method.getName())) {
                        return proxy == args[0];
                    }
                    if ("hashCode".equals(method.getName())) {
                        return System.identityHashCode(proxy);
                    }
                    if ("toString".equals(method.getName())) {
                        return userId + ":" + userUuid;
                    }
                    throw new AssertionError("列表查询不应调用方法：" + method.getName());
                });
    }
}
