package neatlogic.framework.restful.mcp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.restful.annotation.OperationType;
import neatlogic.framework.restful.constvalue.OperationTypeEnum;
import neatlogic.framework.restful.core.IApiComponent;
import neatlogic.framework.restful.core.privateapi.PrivateApiComponentFactory;
import neatlogic.framework.restful.dto.ApiVo;
import neatlogic.framework.restful.enums.ApiType;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ClassUtils;

import java.util.Objects;

public class McpToolMetadataBuilder {
    private static final String META_PREFIX = "com.neatlogic/";

    private McpToolMetadataBuilder() {
    }

    /** 一次读取帮助信息，确保工具说明、Schema 和示例来自同一份声明。 */
    public static JSONObject buildTool(ApiVo api) {
        JSONObject help = getHelp(api);
        JSONArray examples = getExamples(help);
        // tools/list 和接口管理MCP说明共用同一份tool元数据，避免展示与真实调用不一致。
        JSONObject apiObj = new JSONObject();
        apiObj.put("name", getToolName(api));
        apiObj.put("title", api.getName());
        apiObj.put("description", buildDescription($.t(api.getDescription()), examples));
        apiObj.put("inputSchema", getSchemaFromHelpList(help.getJSONArray("input"), false));
        JSONObject annotations = getToolAnnotations(api);
        if (!annotations.isEmpty()) {
            apiObj.put("annotations", annotations);
        }
        JSONObject outputSchema = getOutputSchema(help);
        if (!outputSchema.isEmpty()) {
            apiObj.put("outputSchema", outputSchema);
        }
        JSONObject meta = getMeta(api);
        if (!meta.isEmpty()) {
            apiObj.put("_meta", meta);
        }
        return apiObj;
    }

    public static String getToolName(ApiVo api) {
        String token = api.getToken();
        if (token != null && token.startsWith("/")) {
            token = token.substring(1);
        }
        return token == null ? null : token.replaceAll("[^A-Za-z0-9_.-]", ".");
    }

    /** 读取已翻译的接口帮助；组件不可用时返回空结构。 */
    private static JSONObject getHelp(ApiVo api) {
        IApiComponent comp = PrivateApiComponentFactory.getComponent(api.getHandler(), ApiType.OBJECT, IApiComponent.class);
        JSONObject help = comp == null ? null : comp.help();
        return help == null ? new JSONObject() : help;
    }

    /** 对外提供包含详细字段帮助的输入 Schema。 */
    public static JSONObject getInputSchema(ApiVo api) {
        return getSchemaFromHelpList(getHelp(api).getJSONArray("input"), false);
    }

    /** 对外提供接口已声明的输出 Schema。 */
    public static JSONObject getOutputSchema(ApiVo api) {
        return getOutputSchema(getHelp(api));
    }

    /** 未声明出参时不生成推断的输出约束。 */
    private static JSONObject getOutputSchema(JSONObject help) {
        JSONArray outputList = help.getJSONArray("output");
        if (outputList == null || outputList.isEmpty()) {
            return new JSONObject();
        }
        return getSchemaFromHelpList(outputList, true, true, true);
    }

    public static JSONObject getToolAnnotations(ApiVo api) {
        JSONObject annotations = new JSONObject();
        annotations.put("title", api.getName());
        OperationTypeEnum operationType = getOperationType(api);
        if (operationType == null || Objects.equals(operationType, OperationTypeEnum.SEARCH)) {
            annotations.put("readOnlyHint", true);
            annotations.put("destructiveHint", false);
            annotations.put("idempotentHint", true);
            annotations.put("openWorldHint", false);
            return annotations;
        }
        annotations.put("readOnlyHint", false);
        annotations.put("destructiveHint", Objects.equals(operationType, OperationTypeEnum.DELETE));
        annotations.put("idempotentHint", false);
        annotations.put("openWorldHint", false);
        return annotations;
    }

    /** 扩展元数据只保存接口标识；场景示例统一放在模型可读的 description 中。 */
    public static JSONObject getMeta(ApiVo api) {
        JSONObject meta = new JSONObject();
        meta.put(META_PREFIX + "module", api.getModuleGroup());
        meta.put(META_PREFIX + "token", api.getToken());
        return meta;
    }

    /** 帮助页面直接读取统一注解解析结果，不依赖 MCP 元数据或解析描述文本。 */
    public static JSONArray getExamples(ApiVo api) {
        return getExamples(getHelp(api));
    }

    /** 帮助协议中的 example 始终是场景列表，不兼容旧单对象结构。 */
    private static JSONArray getExamples(JSONObject help) {
        JSONArray examples = help.getJSONArray("example");
        return examples == null ? new JSONArray() : examples;
    }

    /** 将完整场景写入标准 description，让忽略 _meta 的模型也能正确构造 arguments。 */
    static String buildDescription(String description, JSONArray examples) {
        StringBuilder text = new StringBuilder(StringUtils.defaultString(description));
        if (examples != null && !examples.isEmpty()) {
            text.append("\n\n").append($.t("nf.api.example.mcparguments"));
            for (int i = 0; i < examples.size(); i++) {
                JSONObject example = examples.getJSONObject(i);
                text.append("\n\n### ").append(example.getString("title"));
                if (StringUtils.isNotBlank(example.getString("description"))) {
                    text.append("\n").append(example.getString("description"));
                }
                text.append("\n```json\n").append(JSONObject.toJSONString(example.get("example"), true)).append("\n```");
            }
        }
        return text.toString();
    }

    /** 按同一顺序包装完整 tools/call 请求，不向 arguments 混入场景元信息。 */
    public static JSONArray getCallToolExamples(String toolName, JSONArray examples) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < examples.size(); i++) {
            JSONObject example = examples.getJSONObject(i);
            JSONObject params = new JSONObject(true);
            params.put("name", toolName);
            // 场景内容同时出现在帮助列表中，深拷贝避免响应序列化将 arguments 写成 $ref。
            params.put("arguments", JSON.parse(JSON.toJSONString(example.get("example")), Feature.OrderedField));
            JSONObject request = new JSONObject(true);
            request.put("jsonrpc", "2.0");
            request.put("id", i + 1);
            request.put("method", "tools/call");
            request.put("params", params);
            JSONObject item = new JSONObject(true);
            item.put("title", example.getString("title"));
            item.put("description", example.getString("description"));
            item.put("example", request);
            result.add(item);
        }
        return result;
    }

    private static JSONObject getSchemaFromHelpList(JSONArray paramList, boolean allowNestedObject) {
        return getSchemaFromHelpList(paramList, allowNestedObject, false);
    }

    private static JSONObject getSchemaFromHelpList(JSONArray paramList, boolean allowNestedObject, boolean wrapBlankNameWithReturn) {
        return getSchemaFromHelpList(paramList, allowNestedObject, wrapBlankNameWithReturn, false);
    }

    private static JSONObject getSchemaFromHelpList(JSONArray paramList, boolean allowNestedObject, boolean wrapBlankNameWithReturn, boolean loose) {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");
        if (loose) {
            schema.put("additionalProperties", true);
        }
        JSONObject properties = new JSONObject();
        JSONArray requiredList = new JSONArray();
        if (paramList != null) {
            for (int i = 0; i < paramList.size(); i++) {
                JSONObject param = paramList.getJSONObject(i);
                String name = param.getString("name");
                if (StringUtils.isBlank(name)) {
                    if (!wrapBlankNameWithReturn) {
                        continue;
                    }
                    name = "Return";
                }
                JSONObject paramSchema = getParamSchema(param, allowNestedObject, loose);
                if (loose) {
                    putLooseProperty(properties, name, paramSchema);
                } else {
                    properties.put(name, paramSchema);
                }
                if (!loose && param.getBooleanValue("isRequired")) {
                    requiredList.add(name);
                }
            }
        }
        schema.put("properties", properties);
        if (!requiredList.isEmpty()) {
            schema.put("required", requiredList);
        }
        return schema;
    }

    private static JSONObject getParamSchema(JSONObject input, boolean allowNestedObject) {
        return getParamSchema(input, allowNestedObject, false);
    }

    private static JSONObject getParamSchema(JSONObject input, boolean allowNestedObject, boolean loose) {
        JSONObject schema = new JSONObject();
        JSONArray children = input.getJSONArray("children");
        String inputType = input.getString("type");
        if (allowNestedObject && Objects.equals(inputType, "jsonArray")) {
            schema.put("type", "array");
            JSONObject itemSchema = new JSONObject();
            itemSchema.put("type", "object");
            itemSchema.put("properties", buildChildProperties(children, loose));
            if (loose) {
                itemSchema.put("additionalProperties", true);
            }
            schema.put("items", itemSchema);
        } else if (allowNestedObject && children != null && !children.isEmpty()) {
            schema.put("type", "object");
            schema.put("properties", buildChildProperties(children, loose));
            if (loose) {
                schema.put("additionalProperties", true);
            }
        } else {
            schema.put("type", getJsonSchemaType(inputType));
        }
        // 标签与详细帮助共同构成模型可读的字段说明，条件必填不提升为全局 required。
        String description = StringUtils.defaultString(input.getString("description"));
        String help = input.getString("help");
        if (StringUtils.isNotBlank(help) && !Objects.equals(description, help)) {
            description = StringUtils.isBlank(description) ? help : description + "\n" + help;
        }
        if (StringUtils.isNotBlank(description)) {
            schema.put("description", description);
        }
        if (!loose && input.getInteger("maxLength") != null) {
            schema.put("maxLength", input.getInteger("maxLength"));
        }
        String rule = input.getString("rule");
        if (!loose && StringUtils.isNotBlank(rule)) {
            // StringApiParam 的 rule 与枚举一样表示候选值，并非正则表达式。
            if (Objects.equals(input.getString("type"), "enum") || Objects.equals(input.getString("type"), "string")) {
                JSONArray enumList = new JSONArray();
                for (String item : rule.split(",")) {
                    if (StringUtils.isNotBlank(item)) {
                        enumList.add(item);
                    }
                }
                if (!enumList.isEmpty()) {
                    schema.put("enum", enumList);
                }
            } else {
                schema.put("pattern", rule);
            }
        }
        return schema;
    }

    private static JSONObject buildChildProperties(JSONArray children) {
        return buildChildProperties(children, false);
    }

    private static JSONObject buildChildProperties(JSONArray children, boolean loose) {
        JSONObject properties = new JSONObject();
        if (children == null) {
            return properties;
        }
        for (int i = 0; i < children.size(); i++) {
            JSONObject child = children.getJSONObject(i);
            String childName = child.getString("name");
            if (StringUtils.isBlank(childName)) {
                continue;
            }
            properties.put(childName, getParamSchema(child, true, loose));
        }
        return properties;
    }

    private static void putLooseProperty(JSONObject properties, String rawName, JSONObject propertySchema) {
        String[] namePartArray = rawName.split("\\.");
        JSONObject currentProperties = properties;
        for (int i = 0; i < namePartArray.length; i++) {
            String namePart = namePartArray[i];
            boolean isArray = namePart.endsWith("[]");
            String propertyName = isArray ? namePart.substring(0, namePart.length() - 2) : namePart;
            if (StringUtils.isBlank(propertyName)) {
                continue;
            }
            boolean isLast = i == namePartArray.length - 1;
            if (isLast) {
                currentProperties.put(propertyName, propertySchema);
                return;
            }
            JSONObject nextSchema = currentProperties.getJSONObject(propertyName);
            if (nextSchema == null) {
                nextSchema = isArray ? getLooseArrayObjectSchema() : getLooseObjectSchema();
                currentProperties.put(propertyName, nextSchema);
            }
            if (isArray) {
                currentProperties = nextSchema.getJSONObject("items").getJSONObject("properties");
            } else {
                currentProperties = nextSchema.getJSONObject("properties");
            }
        }
    }

    private static JSONObject getLooseObjectSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "object");
        schema.put("properties", new JSONObject());
        schema.put("additionalProperties", true);
        return schema;
    }

    private static JSONObject getLooseArrayObjectSchema() {
        JSONObject schema = new JSONObject();
        schema.put("type", "array");
        JSONObject itemSchema = getLooseObjectSchema();
        schema.put("items", itemSchema);
        return schema;
    }

    private static OperationTypeEnum getOperationType(ApiVo api) {
        IApiComponent comp = PrivateApiComponentFactory.getComponent(api.getHandler(), ApiType.OBJECT, IApiComponent.class);
        if (comp == null) {
            return null;
        }
        Class<?> clazz = ClassUtils.getUserClass(comp.getClass());
        OperationType operationType = clazz.getAnnotation(OperationType.class);
        return operationType == null ? null : operationType.type();
    }

    private static String getJsonSchemaType(String type) {
        if (Objects.equals(type, "int") || Objects.equals(type, "long")) {
            return "integer";
        } else if (Objects.equals(type, "double")) {
            return "number";
        } else if (Objects.equals(type, "boolean")) {
            return "boolean";
        } else if (Objects.equals(type, "jsonObject")) {
            return "object";
        } else if (Objects.equals(type, "jsonArray")) {
            return "array";
        }
        return "string";
    }
}
