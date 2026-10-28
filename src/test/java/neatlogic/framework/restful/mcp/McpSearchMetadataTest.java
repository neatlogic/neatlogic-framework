package neatlogic.framework.restful.mcp;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.common.constvalue.IEnum;
import neatlogic.framework.common.dto.ValueTextVo;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.*;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.dto.ApiExampleVo;
import java.util.List;
import org.junit.*;
import org.springframework.aop.framework.ProxyFactory;

/** 检索资料来自真实 API 声明，同时与完整调用定义保持独立。 */
public class McpSearchMetadataTest {
    /** 示例和默认值只影响完整调用材料，检索资料保留用途、字段帮助和枚举。 */
    @Test public void projectsOnlyDeclaredSearchFields() {
        SearchApi component = new SearchApi();
        String handler = component.getClass().getName();
        IApiComponent previous = PrivateApiComponentFactory.getComponentMap().put(handler, component);
        try {
            ApiVo api = new ApiVo(); api.setHandler(handler); api.setToken("example/search");
            api.setModuleGroup("example"); api.setName("common.name"); api.setDescription("common.description");
            JSONObject metadata = McpToolMetadataBuilder.buildSearchMetadata(api);
            Assert.assertEquals(0, component.examplesRead);
            Assert.assertEquals("example.search", metadata.getString("name"));
            Assert.assertEquals("example/search", metadata.getString("token"));
            Assert.assertEquals("example", metadata.getString("module"));
            String parameters = metadata.getString("parameters");
            Assert.assertTrue(parameters.contains("action"));
            Assert.assertTrue(parameters.contains("search_targets,target_alerts"));
            Assert.assertTrue(parameters.contains(component.help().getJSONArray("input").getJSONObject(0).getString("description")));
            Assert.assertFalse(metadata.toJSONString().contains("example_only_marker"));
            Assert.assertFalse(metadata.toJSONString().contains("default_only_marker"));
            Assert.assertFalse(metadata.containsKey("inputSchema"));
            Assert.assertTrue(parameters.contains("向量化模型"));
            Assert.assertTrue(parameters.contains("语义向量检索"));
            Assert.assertFalse(parameters.contains("undeclared_option"));
            Assert.assertFalse(parameters.contains("enum_example_marker"));
            JSONObject full = McpToolMetadataBuilder.buildTool(api);
            Assert.assertTrue(full.getString("description").contains("example_only_marker"));
            Assert.assertNotNull(full.getJSONObject("inputSchema"));
            Assert.assertEquals(List.of("embedding"), full.getJSONObject("inputSchema").getJSONObject("properties")
                    .getJSONObject("modelType").getJSONArray("enum"));
            Assert.assertTrue(component.examplesRead > 0);
            Assert.assertFalse(full.toJSONString().contains("$ref"));
        } finally {
            if (previous == null) PrivateApiComponentFactory.getComponentMap().remove(handler);
            else PrivateApiComponentFactory.getComponentMap().put(handler, previous);
        }
    }
    /** Spring 代理不应使参数声明丢失，也不能为了读取声明而调用帮助或示例方法。 */
    @Test public void readsInputDeclarationsBehindSpringProxies() {
        SearchApi component = new SearchApi();
        String handler = "mcp-search-proxy-test";
        IApiComponent previous = PrivateApiComponentFactory.getComponentMap().get(handler);
        try {
            for (boolean classProxy : List.of(false, true)) {
                ProxyFactory factory = new ProxyFactory(component);
                factory.setInterfaces(IApiComponent.class);
                factory.setProxyTargetClass(classProxy);
                PrivateApiComponentFactory.getComponentMap().put(handler, (IApiComponent) factory.getProxy());
                ApiVo api = new ApiVo(); api.setHandler(handler); api.setToken("example/search");
                JSONObject metadata = McpToolMetadataBuilder.buildSearchMetadata(api);
                Assert.assertTrue(metadata.getString("parameters").contains("search_targets,target_alerts"));
                Assert.assertEquals(0, component.examplesRead);
            }
        } finally {
            if (previous == null) PrivateApiComponentFactory.getComponentMap().remove(handler);
            else PrivateApiComponentFactory.getComponentMap().put(handler, previous);
        }
    }

    /** 模拟既有枚举的两种标准 value/text 返回形态，不引入翻译推断。 */
    public enum ModelType implements IEnum<Object> {
        EMBEDDING;
        /** 参数合法值保持稳定。 */
        @Override public String getValue() { return "embedding"; }
        /** 额外字段和非声明选项不能进入检索资料。 */
        @Override public List<Object> getValueTextList() {
            return List.of(new ValueTextVo("embedding", "向量化模型"),
                    new JSONObject().fluentPut("value", "embedding").fluentPut("description", "语义向量检索").fluentPut("example", "enum_example_marker"),
                    new JSONObject().fluentPut("value", "other").fluentPut("text", "undeclared_option"));
        }
    }

    /** 仅提供帮助声明，不执行真实服务。 */
    public static class SearchApi extends PrivateApiComponentBase {
        private int examplesRead;
        /** 记录程序化示例访问，静态检索投影不得进入此路径。 */
        @Override public List<ApiExampleVo> example() { examplesRead++; return List.of(); }
        @Override public String getToken() { return "example/search"; }
        @Override public String getName() { return "common.name"; }
        @Override public String getDescription() { return "common.description"; }
        /** 真实注解用于验证帮助投影的字段白名单。 */
        @Input({@Param(name="modelType", type=ApiParamType.ENUM, member=ModelType.class, desc="common.type"),
                @Param(name="action", type=ApiParamType.ENUM, rule="search_targets,target_alerts", desc="common.type", help="common.description"),
                @Param(name="keyword", type=ApiParamType.STRING, desc="common.name", defaultValue="default_only_marker")})
        @Example(title="common.name", description="common.description", example="{\"keyword\":\"example_only_marker\"}")
        @Description(desc="common.description")
        @Override public Object myDoService(JSONObject input) { return null; }
    }
}
