package neatlogic.framework.restful.mcp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import java.util.Map;
import neatlogic.framework.common.constvalue.ApiParamType;
import neatlogic.framework.restful.annotation.Input;
import neatlogic.framework.restful.annotation.Output;
import neatlogic.framework.restful.annotation.Param;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentBase;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.dto.ApiVo;
import org.junit.Assert;
import org.junit.Test;

/** 验证通用显式标量数组从 API 帮助到 MCP 的类型契约，不依赖任何业务模块。 */
public class McpScalarArraySchemaTest {
    /** 包装类型、基本类型和旧数组同时存在时，只有显式标量声明生成元素 Schema。 */
    @Test public void publishesDeclaredItemsWithoutGuessingLegacyArrays() {
        ArrayApi component = new ArrayApi();
        String handler = component.getClass().getName();
        IApiComponent previous = PrivateApiComponentFactory.getComponentMap().put(handler, component);
        try {
            ApiVo api = new ApiVo(); api.setHandler(handler); api.setToken(component.getToken());
            api.setName("arrays"); api.setDescription("arrays");
            JSONArray help = component.help().getJSONArray("input");
            Assert.assertEquals("long", help.getJSONObject(0).getString("itemType"));
            Assert.assertFalse(help.getJSONObject(help.size() - 1).containsKey("itemType"));
            JSONObject properties = McpToolMetadataBuilder.getInputSchema(api).getJSONObject("properties");
            Map<String, String> expected = Map.of("ids", "integer", "counts", "integer", "flags", "boolean",
                    "scores", "number", "labels", "string");
            expected.forEach((name, type) -> {
                Assert.assertEquals("array", properties.getJSONObject(name).getString("type"));
                Assert.assertEquals(type, properties.getJSONObject(name).getJSONObject("items").getString("type"));
            });
            Assert.assertFalse(properties.getJSONObject("legacy").containsKey("items"));
            JSONObject outputs = McpToolMetadataBuilder.getOutputSchema(api).getJSONObject("properties");
            Assert.assertEquals("integer", outputs.getJSONObject("ids").getJSONObject("items").getString("type"));
            Assert.assertEquals("object", outputs.getJSONObject("legacy").getJSONObject("items").getString("type"));
        } finally {
            if (previous == null) PrivateApiComponentFactory.getComponentMap().remove(handler);
            else PrivateApiComponentFactory.getComponentMap().put(handler, previous);
        }
    }

    /** 仅供帮助反射使用的接口声明，不执行请求。 */
    public static class ArrayApi extends PrivateApiComponentBase {
        /** 测试接口标识。 */
        @Override public String getToken() { return "test/scalar/arrays"; }
        /** 测试标题。 */
        @Override public String getName() { return "arrays"; }
        /** 无额外配置。 */
        @Override public String getConfig() { return null; }
        /** 同一注解同时约束帮助与 MCP 输入输出。 */
        @Input({
                @Param(name="ids", type=ApiParamType.JSONARRAY, explode=Long[].class),
                @Param(name="counts", type=ApiParamType.JSONARRAY, explode=int[].class),
                @Param(name="flags", type=ApiParamType.JSONARRAY, explode=Boolean[].class),
                @Param(name="scores", type=ApiParamType.JSONARRAY, explode=double[].class),
                @Param(name="labels", type=ApiParamType.JSONARRAY, explode=String[].class),
                @Param(name="legacy", type=ApiParamType.JSONARRAY)
        })
        @Output({
                @Param(name="ids", explode=Long[].class),
                @Param(name="legacy", type=ApiParamType.JSONARRAY)
        })
        @Override public Object myDoService(JSONObject input) { return null; }
    }
}
