package neatlogic.framework.restful.mcp;

import com.alibaba.fastjson.JSONArray;
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

    public static JSONObject buildTool(ApiVo api) {
        // tools/list 和接口管理MCP说明共用同一份tool元数据，避免展示与真实调用不一致。
        JSONObject apiObj = new JSONObject();
        apiObj.put("name", getToolName(api));
        apiObj.put("title", api.getName());
        apiObj.put("description", $.t(api.getDescription()));
        apiObj.put("inputSchema", getInputSchema(api));
        JSONObject annotations = getToolAnnotations(api);
        if (!annotations.isEmpty()) {
            apiObj.put("annotations", annotations);
        }
        JSONObject outputSchema = getOutputSchema(api);
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

    public static JSONObject getInputSchema(ApiVo api) {
        IApiComponent comp = PrivateApiComponentFactory.getComponent(api.getHandler(), ApiType.OBJECT, IApiComponent.class);
        JSONObject helpObj = comp == null ? null : comp.help();
        return getSchemaFromHelpList(helpObj == null ? null : helpObj.getJSONArray("input"), false);
    }

    public static JSONObject getOutputSchema(ApiVo api) {
        IApiComponent comp = PrivateApiComponentFactory.getComponent(api.getHandler(), ApiType.OBJECT, IApiComponent.class);
        JSONObject helpObj = comp == null ? null : comp.help();
        JSONArray outputList = helpObj == null ? null : helpObj.getJSONArray("output");
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

    public static JSONObject getMeta(ApiVo api) {
        JSONObject meta = new JSONObject();
        meta.put(META_PREFIX + "module", api.getModuleGroup());
        meta.put(META_PREFIX + "token", api.getToken());
        Object example = getExample(api);
        if (example != null) {
            meta.put(META_PREFIX + "example", example);
        }
        return meta;
    }

    public static Object getExample(ApiVo api) {
        IApiComponent comp = PrivateApiComponentFactory.getComponent(api.getHandler(), ApiType.OBJECT, IApiComponent.class);
        if (comp == null) {
            return null;
        }
        JSONObject helpObj = comp.help();
        return helpObj == null ? null : helpObj.get("example");
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
        if (StringUtils.isNotBlank(input.getString("description"))) {
            schema.put("description", input.getString("description"));
        }
        if (!loose && input.getInteger("maxLength") != null) {
            schema.put("maxLength", input.getInteger("maxLength"));
        }
        String rule = input.getString("rule");
        if (!loose && StringUtils.isNotBlank(rule)) {
            if (Objects.equals(input.getString("type"), "enum")) {
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
