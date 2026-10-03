package neatlogic.framework.dao.mapper;

import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 验证系统用户权限反查的生产 SQL 绑定和空候选列表边界，不连接数据库。
 */
public class UserMapperSystemUserAuthTest {
    private Configuration configuration;
    private XNode selectNode;

    /**
     * 从生产 Mapper 读取目标语句，隔离无关 Mapper 缓存初始化。
     */
    @Before
    public void loadStatement() throws Exception {
        configuration = new Configuration();
        try (InputStream input = UserMapper.class.getResourceAsStream("UserMapper.xml")) {
            Assert.assertNotNull("生产 UserMapper XML 应随模块资源提供", input);
            XPathParser parser = new XPathParser(input, true, null, new XMLMapperEntityResolver());
            Assert.assertEquals(UserMapper.class.getName(), parser.evalNode("/mapper").getStringAttribute("namespace"));
            selectNode = parser.evalNode("/mapper/select[@id='getUserUuidListByAuthAndUserUuidList']");
            Assert.assertNotNull(selectNode);
            Assert.assertEquals("false", selectNode.getStringAttribute("useCache"));
            Assert.assertEquals("java.lang.String", selectNode.getStringAttribute("resultType"));
        }
    }

    /**
     * 正常候选列表使用绑定参数，只查直接授权表，不要求用户表存在对应记录。
     */
    @Test
    public void shouldBindAuthAndCandidateUuidsWithoutJoiningUserTable() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("auth", "USER_MODIFY");
        parameters.put("userUuidList", new ArrayList<>(Arrays.asList("system", "future-system-uuid")));
        BoundSql boundSql = render(parameters);
        String sql = normalize(boundSql.getSql());

        Assert.assertTrue(sql.startsWith("SELECT DISTINCT `user_uuid` FROM `user_authority` WHERE `auth` = ?"));
        Assert.assertFalse(sql.contains("JOIN"));
        Assert.assertFalse(sql.contains("`user`"));
        Assert.assertTrue(sql.contains("AND `user_uuid` IN ( ? , ? )"));
        Assert.assertFalse(sql.contains("system"));
        Assert.assertEquals(3, boundSql.getParameterMappings().size());
        Assert.assertEquals("auth", boundSql.getParameterMappings().get(0).getProperty());
        Assert.assertEquals("system", boundSql.getAdditionalParameter(boundSql.getParameterMappings().get(1).getProperty()));
        Assert.assertEquals("future-system-uuid", boundSql.getAdditionalParameter(boundSql.getParameterMappings().get(2).getProperty()));
    }

    /**
     * 空列表和空值都必须生成始终不匹配的约束，不得返回该权限的全部用户。
     */
    @Test
    public void shouldReturnNoCandidatesForEmptyOrNullList() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("auth", "USER_MODIFY");
        parameters.put("userUuidList", new ArrayList<>());
        assertNoCandidates(render(parameters));

        parameters.put("userUuidList", null);
        assertNoCandidates(render(parameters));
    }

    /**
     * 使用 MyBatis 动态 SQL 引擎渲染生产语句，验证真实集合展开行为。
     */
    private BoundSql render(Map<String, Object> parameters) {
        return new XMLLanguageDriver().createSqlSource(configuration, selectNode, Map.class).getBoundSql(parameters);
    }

    /**
     * 检查空候选情况下保留权限条件和禁止匹配条件。
     */
    private void assertNoCandidates(BoundSql boundSql) {
        String sql = normalize(boundSql.getSql());
        Assert.assertTrue(sql.contains("WHERE `auth` = ? AND 1 = 0"));
        Assert.assertFalse(sql.contains("IN ("));
        Assert.assertEquals(1, boundSql.getParameterMappings().size());
    }

    /**
     * 合并 XML 缩进空白，让断言只关注查询条件。
     */
    private String normalize(String sql) {
        return sql.trim().replaceAll("\\s+", " ");
    }
}
