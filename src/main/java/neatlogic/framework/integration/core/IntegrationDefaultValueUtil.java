package neatlogic.framework.integration.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import neatlogic.framework.exception.integration.IntegrationDefaultValueInvalidException;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.integration.dto.PatternVo;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.*;

/** 默认值的保存校验、结构合并和运行补齐共用同一规则，配置仍存放在原 JSON 中。 */
public final class IntegrationDefaultValueUtil {
    private static final Set<String> TYPES = new HashSet<>(Arrays.asList(
            "string", "number", "int", "boolean", "enum", "date", "array", "object"));
    private static final String DATE_ERROR = "日期必须符合 yyyy-MM、yyyy-MM-dd、yyyy-MM-dd HH:mm 或 yyyy-MM-dd HH:mm:ss，且为有效日期时间";
    // uuuu 配合 STRICT 校验真实日历；对外仍使用用户熟悉的 yyyy 文案，不进行时区转换。
    private static final DateTimeFormatter DATE_MONTH = DateTimeFormatter.ofPattern("uuuu-MM", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_DAY = DateTimeFormatter.ofPattern("uuuu-MM-dd", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_MINUTE = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter DATE_SECOND = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss", Locale.ROOT).withResolverStyle(ResolverStyle.STRICT);

    private IntegrationDefaultValueUtil() {
    }

    /** 保存前仅清理结构元数据中的历史枚举选项，不遍历 value 中同名的业务字段。 */
    public static void normalizeForSave(JSONObject config) {
        JSONObject param = IntegrationPatternUtil.getParamConfig(config);
        if (param == null) {
            return;
        }
        JSONArray custom = param.getJSONArray("paramList");
        if (custom != null) {
            for (int i = 0; i < custom.size(); i++) {
                if (custom.get(i) instanceof Map) {
                    normalizeDefinition(custom.getJSONObject(i));
                }
            }
        }
        JSONArray builtin = param.getJSONArray("builtinDefaultList");
        if (builtin != null) {
            for (int i = 0; i < builtin.size(); i++) {
                if (builtin.get(i) instanceof Map) {
                    // 非当前处理器的草稿也清理旧元数据，默认值及处理器定位信息保持原样。
                    normalizeDefaultConfig(builtin.getJSONObject(i));
                }
            }
        }
    }

    private static void normalizeDefinition(JSONObject definition) {
        if ("enum".equals(normalizeType(definition.getString("type")))) {
            definition.remove("options");
        }
        normalizeDefaultConfig(definition);
        Object children = definition.get("children");
        if (children instanceof List) {
            JSONArray list = definition.getJSONArray("children");
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) instanceof Map) {
                    normalizeDefinition(list.getJSONObject(i));
                }
            }
        }
    }

    private static void normalizeDefaultConfig(JSONObject entry) {
        if (entry.get("defaultConfig") instanceof Map) {
            Object schema = entry.getJSONObject("defaultConfig").get("schema");
            if (schema instanceof Map) {
                removeSchemaOptions((Map<?, ?>) schema);
            }
        }
    }

    /** 只遍历 schema 的 items/children；运行时在结构副本上调用，处理器原始规范不受影响。 */
    private static void removeSchemaOptions(Map<?, ?> schema) {
        schema.remove("options");
        Object items = schema.get("items");
        if (items instanceof Map) {
            removeSchemaOptions((Map<?, ?>) items);
        }
        Object children = schema.get("children");
        if (children instanceof List) {
            for (Object child : (List<?>) children) {
                if (child instanceof Map) {
                    removeSchemaOptions((Map<?, ?>) child);
                }
            }
        }
    }

    /** 禁用的草稿允许保留结构；只有明确启用的默认值参与校验和补齐。 */
    public static boolean isEnabled(JSONObject config) {
        if (config == null) {
            return false;
        }
        Object enabled = config.get("enabled");
        return Boolean.TRUE.equals(enabled) || (enabled instanceof Number && ((Number) enabled).doubleValue() == 1);
    }

    /** 保存时检查当前规范的有效配置，切换规范时保留的其他处理器配置不参与执行。 */
    public static void validate(IIntegrationHandler handler, JSONObject config) {
        validateBuiltinEntries(handler, config);
        validateCustomDefaults(config, null);
        for (PatternVo pattern : IntegrationPatternUtil.merge(handler, config)) {
            validateDefault(pattern);
        }
    }

    public static void validateDefault(PatternVo pattern) {
        JSONObject defaults = pattern.getDefaultConfig();
        if (!isEnabled(defaults)) {
            return;
        }
        String path = pattern.getMode() + "." + pattern.getName();
        if (!defaults.containsKey("value")) {
            fail(path, "已启用但未填写默认值");
        }
        JSONObject schema = getEffectiveSchema(pattern);
        validateSchema(schema, path, true);
        validateValue(defaults.get("value"), schema, path, false);
    }

    /** 处理器结构优先；补充结构只能完善未声明的嵌套数组或对象。 */
    public static JSONObject getEffectiveSchema(PatternVo pattern) {
        JSONObject defaults = pattern.getDefaultConfig();
        if (defaults != null && defaults.containsKey("schema") && !(defaults.get("schema") instanceof Map)) {
            fail(pattern.getMode() + "." + pattern.getName(), "参数结构格式不正确");
        }
        JSONObject supplement = defaults == null ? null : defaults.getJSONObject("schema");
        // 旧选项已不参与默认值约束，先在副本清理，避免历史格式或选项变动导致失效。
        if (supplement != null) {
            supplement = (JSONObject) copyJson(supplement);
            removeSchemaOptions(supplement);
        }
        return mergeSchema(declaredSchema(pattern), supplement, pattern.getMode() + "." + pattern.getName());
    }

    /** 不覆盖任何显式字段（包括 null）；只在缺字段时整体复制默认值，禁止数组行合并。 */
    public static JSONObject apply(IIntegrationHandler handler, JSONObject config, String mode, JSONObject values) {
        validateBuiltinEntries(handler, config);
        validateCustomDefaults(config, mode);
        List<PatternVo> patterns = IntegrationPatternUtil.merge(handler, config, mode);
        JSONObject result = values;
        for (PatternVo pattern : patterns) {
            if (isEnabled(pattern.getDefaultConfig())) {
                // 即使调用方已经传值，非法配置也不能在运行时悄悄绕过校验。
                validateDefault(pattern);
                if (result == values) {
                    result = values == null ? new JSONObject() : (JSONObject) copyJson(values);
                }
                if (!result.containsKey(pattern.getName())) {
                    result.put(pattern.getName(), copyJson(pattern.getDefaultConfig().get("value")));
                }
            }
        }
        return result;
    }

    /** 仅成功且没有转换错误的 JSON 对象可补齐，原始响应及错误信息保持不变。 */
    public static void applyOutput(IIntegrationHandler handler, JSONObject config, IntegrationResultVo result) {
        if (result.getStatusCode() < 200 || result.getStatusCode() >= 300 || StringUtils.isNotBlank(result.getError())
                || StringUtils.isBlank(result.getTransformedResult())) {
            return;
        }
        Object parsed;
        try {
            parsed = JSON.parse(result.getTransformedResult());
        } catch (JSONException ex) {
            return;
        }
        if (parsed instanceof JSONObject) {
            JSONObject filled = apply(handler, config, "output", (JSONObject) parsed);
            if (filled != parsed) {
                result.setTransformedResult(JSON.toJSONString(filled, SerializerFeature.WriteMapNullValue));
            }
        }
    }

    /** 递归复制 JSON 容器并保留 null；每次请求都与保存配置及其他请求隔离。 */
    public static Object copyJson(Object value) {
        if (value instanceof Map) {
            JSONObject copy = new JSONObject(true);
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                copy.put(String.valueOf(entry.getKey()), copyJson(entry.getValue()));
            }
            return copy;
        }
        if (value instanceof Collection) {
            JSONArray copy = new JSONArray();
            for (Object item : (Collection<?>) value) {
                copy.add(copyJson(item));
            }
            return copy;
        }
        return value;
    }

    private static JSONObject declaredSchema(PatternVo pattern) {
        JSONObject schema = new JSONObject(true);
        schema.put("type", normalizeType(pattern.getType()));
        schema.put("name", pattern.getName());
        schema.put("isRequired", pattern.getIsRequired());
        if (pattern.getChildren() != null && !pattern.getChildren().isEmpty()) {
            JSONArray children = new JSONArray();
            for (PatternVo child : pattern.getChildren()) {
                if (child != null) {
                    children.add(declaredSchema(child));
                }
            }
            if ("array".equals(schema.getString("type"))) {
                JSONObject items = new JSONObject(true);
                items.put("type", "object");
                items.put("children", children);
                schema.put("items", items);
            } else {
                schema.put("children", children);
            }
        }
        return schema;
    }

    /** 保存时也检查被历史同名定义遮蔽的自定义默认值，避免配置中潜伏非法值。 */
    private static void validateCustomDefaults(JSONObject config, String mode) {
        JSONObject param = IntegrationPatternUtil.getParamConfig(config);
        JSONArray custom = param == null ? null : param.getJSONArray("paramList");
        if (custom != null) {
            for (int i = 0; i < custom.size(); i++) {
                JSONObject entry = custom.getJSONObject(i);
                if (entry != null && (mode == null || mode.equals(entry.getString("mode")))) {
                    validateDefault(entry.toJavaObject(PatternVo.class));
                }
            }
        }
    }

    /** 当前处理器中已删除的参数不能继续静默使用；其他处理器草稿保留供切换后恢复。 */
    private static void validateBuiltinEntries(IIntegrationHandler handler, JSONObject config) {
        JSONObject param = IntegrationPatternUtil.getParamConfig(config);
        JSONArray entries = param == null ? null : param.getJSONArray("builtinDefaultList");
        if (handler == null || entries == null) {
            return;
        }
        Set<List<String>> definitions = new HashSet<>();
        for (List<PatternVo> patterns : Arrays.asList(handler.getInputPattern(), handler.getOutputPattern())) {
            if (patterns != null) {
                for (PatternVo pattern : patterns) {
                    if (pattern != null) {
                        definitions.add(Arrays.asList(pattern.getMode(), pattern.getName()));
                    }
                }
            }
        }
        Set<List<String>> seen = new HashSet<>();
        for (int i = 0; i < entries.size(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            if (entry != null && handler.getHandler().equals(entry.getString("handler"))) {
                List<String> key = Arrays.asList(entry.getString("mode"), entry.getString("name"));
                String path = entry.getString("mode") + "." + entry.getString("name");
                if (!seen.add(key)) {
                    fail(path, "固定参数默认值重复");
                }
                if (isEnabled(entry.getJSONObject("defaultConfig")) && !definitions.contains(key)) {
                    fail(path, "参数结构已变化，请重新配置默认值");
                }
            }
        }
    }

    private static JSONObject mergeSchema(JSONObject declared, JSONObject supplement, String path) {
        if (supplement != null) {
            validateNodeShape(supplement, path);
        }
        if (declared == null) {
            return supplement == null ? new JSONObject() : (JSONObject) copyJson(supplement);
        }
        JSONObject merged = supplement == null ? new JSONObject(true) : (JSONObject) copyJson(supplement);
        String type = normalizeType(declared.getString("type"));
        if (StringUtils.isNotBlank(type)) {
            if (supplement != null && StringUtils.isNotBlank(supplement.getString("type"))
                    && !type.equals(normalizeType(supplement.getString("type")))) {
                fail(path, "参数结构已变化，请重新配置默认值");
            }
            merged.put("type", type);
        }
        if (declared.containsKey("name")) {
            merged.put("name", declared.get("name"));
        }
        if (declared.get("isRequired") != null) {
            if (supplement != null && supplement.containsKey("isRequired")
                    && required(declared) != required(supplement)) {
                fail(path, "固定字段的必填要求不能修改，请重新配置默认值");
            }
            merged.put("isRequired", declared.get("isRequired"));
        }
        if (declared.getJSONObject("items") != null) {
            merged.put("items", mergeSchema(declared.getJSONObject("items"),
                    supplement == null ? null : supplement.getJSONObject("items"), path + "[]"));
        }
        JSONArray fixedChildren = declared.getJSONArray("children");
        if (fixedChildren != null && !fixedChildren.isEmpty()) {
            JSONArray customChildren = supplement == null ? null : supplement.getJSONArray("children");
            Map<String, JSONObject> additions = childrenByName(customChildren, path);
            // 页面保存完整结构快照；提供列清单时须与固定结构一致，识别规范新增或删除列。
            if (customChildren != null && additions.size() != fixedChildren.size()) {
                fail(path, "固定子字段结构已变化，请重新配置默认值");
            }
            JSONArray children = new JSONArray();
            for (int i = 0; i < fixedChildren.size(); i++) {
                JSONObject child = fixedChildren.getJSONObject(i);
                String name = child.getString("name");
                children.add(mergeSchema(child, additions.remove(name), path + "." + name));
            }
            if (!additions.isEmpty()) {
                fail(path, "固定子字段结构已变化，请重新配置默认值");
            }
            merged.put("children", children);
        }
        return merged;
    }

    private static Map<String, JSONObject> childrenByName(JSONArray children, String path) {
        Map<String, JSONObject> result = new LinkedHashMap<>();
        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                if (!(children.get(i) instanceof Map)) {
                    fail(path, "对象列结构格式不正确");
                }
                JSONObject child = children.getJSONObject(i);
                String name = child == null ? null : child.getString("name");
                if (StringUtils.isBlank(name) || result.putIfAbsent(name, child) != null) {
                    fail(path, "对象列名不能为空或重复");
                }
            }
        }
        return result;
    }

    private static void validateSchema(JSONObject schema, String path, boolean requireStructure) {
        validateNodeShape(schema, path);
        String type = normalizeType(schema.getString("type"));
        schema.put("type", type);
        if (!TYPES.contains(type)) {
            fail(path, "请选择有效的数据类型或数组元素类型");
        }
        if ("array".equals(type)) {
            JSONObject items = schema.getJSONObject("items");
            if (items == null) {
                if (requireStructure) {
                    fail(path, "请先配置数组元素结构");
                }
                return;
            }
            validateSchema(items, path + "[]", requireStructure);
        } else if ("object".equals(type)) {
            JSONArray children = schema.getJSONArray("children");
            if (children == null || children.isEmpty()) {
                if (requireStructure) {
                    fail(path, "请先配置对象列结构");
                }
                return;
            }
            for (Map.Entry<String, JSONObject> child : childrenByName(children, path).entrySet()) {
                // 子字段允许直接填写 JSON；已有结构仍校验，未声明结构时仅检查容器及 JSON 值。
                validateSchema(child.getValue(), path + "." + child.getKey(), false);
            }
        }
    }

    /** 不将字符串等错误 JSON 类型自动转换成结构对象，保持与页面校验一致。 */
    private static void validateNodeShape(JSONObject schema, String path) {
        if (schema.containsKey("items") && !(schema.get("items") instanceof Map)) {
            fail(path, "数组元素结构格式不正确");
        }
        if (schema.containsKey("children") && !(schema.get("children") instanceof List)) {
            fail(path, "对象列结构格式不正确");
        }
    }

    private static void validateValue(Object value, JSONObject schema, String path, boolean nested) {
        // 顶层表格仍要求完整结构；对象子字段可使用未声明内部结构的 JSON 容器。
        validateSchema(schema, path, !nested);
        String type = schema.getString("type");
        switch (type) {
            // 日期仍保存原字符串，但保存及运行都检查严格格式；不修改全局日期校验器。
            case "enum":
            case "date":
            case "string":
                if (!(value instanceof String)) {
                    fail(path, "必须为字符串");
                }
                // 必填文本与前端 trim 判空保持一致，可选文本仍允许明确保存空字符串。
                if (required(schema) && isBlankDefaultText((String) value)) {
                    fail(path, "必填字段未填写");
                }
                if ("date".equals(type) && !isValidDefaultDate((String) value)) {
                    fail(path, DATE_ERROR);
                }
                break;
            case "number":
            case "int":
                if (!(value instanceof Number) || !Double.isFinite(((Number) value).doubleValue())) {
                    fail(path, "必须为有限数字");
                }
                if ("int".equals(type) && new BigDecimal(value.toString()).stripTrailingZeros().scale() > 0) {
                    fail(path, "必须为整数");
                }
                break;
            case "boolean":
                if (!(value instanceof Boolean)) {
                    fail(path, "必须为布尔值 true 或 false");
                }
                break;
            case "array":
                if (!(value instanceof List)) {
                    fail(path, "必须为数组");
                }
                List<?> rows = (List<?>) value;
                JSONObject items = schema.getJSONObject("items");
                if (nested && items == null) {
                    validateJsonValue(value, path);
                    break;
                }
                for (int i = 0; i < rows.size(); i++) {
                    // 数组元素继承所在字段的上下文，顶层数组不能借空对象元素绕过结构要求。
                    validateValue(rows.get(i), items, path + "[第 " + (i + 1) + " 项]", nested);
                }
                break;
            case "object":
                if (!(value instanceof Map)) {
                    fail(path, "必须为对象");
                }
                Map<?, ?> object = (Map<?, ?>) value;
                Map<String, JSONObject> columns = childrenByName(schema.getJSONArray("children"), path);
                if (nested && columns.isEmpty()) {
                    validateJsonValue(value, path);
                    break;
                }
                for (Map.Entry<String, JSONObject> child : columns.entrySet()) {
                    String childPath = path + "." + child.getKey();
                    if (!object.containsKey(child.getKey())) {
                        if (required(child.getValue())) {
                            fail(childPath, "必填字段未填写");
                        }
                    } else {
                        validateValue(object.get(child.getKey()), child.getValue(), childPath, true);
                    }
                }
                for (Object key : object.keySet()) {
                    if (!columns.containsKey(key)) {
                        fail(path + "." + key, "字段不在已声明的对象列结构中");
                    }
                }
                break;
            default:
                fail(path, "不支持的数据类型");
        }
    }

    /** 日期只接受四种完整格式和真实日历字段，不补全、不裁剪，也不转换原字符串。 */
    private static boolean isValidDefaultDate(String value) {
        if (!value.matches("(?!0000)[0-9]{4}-[0-9]{2}(?:-[0-9]{2}(?: [0-9]{2}:[0-9]{2}(?::[0-9]{2})?)?)?")) {
            return false;
        }
        try {
            switch (value.length()) {
                case 7: YearMonth.parse(value, DATE_MONTH); break;
                case 10: LocalDate.parse(value, DATE_DAY); break;
                case 16: LocalDateTime.parse(value, DATE_MINUTE); break;
                case 19: LocalDateTime.parse(value, DATE_SECOND); break;
                default: return false;
            }
            return true;
        } catch (DateTimeException exception) {
            return false;
        }
    }

    /** 自由 JSON 也必须由合法容器和标量组成，禁止非有限数字或 Java 对象混入配置。 */
    private static void validateJsonValue(Object value, String path) {
        if (value == null || value instanceof String || value instanceof Boolean) {
            return;
        }
        if (value instanceof Number) {
            if (!Double.isFinite(((Number) value).doubleValue())) {
                fail(path, "必须为有限数字");
            }
            return;
        }
        if (value instanceof List) {
            List<?> items = (List<?>) value;
            for (int i = 0; i < items.size(); i++) {
                validateJsonValue(items.get(i), path + "[第 " + (i + 1) + " 项]");
            }
            return;
        }
        if (value instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (!(entry.getKey() instanceof String)) {
                    fail(path, "JSON 对象字段名必须为字符串");
                }
                validateJsonValue(entry.getValue(), path + "." + entry.getKey());
            }
            return;
        }
        fail(path, "必须为合法 JSON 值");
    }

    private static boolean required(JSONObject schema) {
        return Boolean.TRUE.equals(schema.get("isRequired")) || Integer.valueOf(1).equals(schema.get("isRequired"));
    }

    /** 与 ECMAScript String.trim 的空白集合一致，包含不换行空格与 BOM，不额外接受控制字符。 */
    private static boolean isBlankDefaultText(String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!((c >= 0x0009 && c <= 0x000d) || c == 0x0020 || c == 0x00a0
                    || c == 0x1680 || (c >= 0x2000 && c <= 0x200a) || c == 0x2028
                    || c == 0x2029 || c == 0x202f || c == 0x205f || c == 0x3000
                    || c == 0xfeff)) {
                return false;
            }
        }
        return true;
    }

    private static String normalizeType(String type) {
        if (type == null) {
            return null;
        }
        type = type.toLowerCase(Locale.ROOT);
        if ("jsonarray".equals(type)) {
            return "array";
        }
        if ("jsonobject".equals(type)) {
            return "object";
        }
        if ("integer".equals(type)) {
            return "int";
        }
        if ("bool".equals(type)) {
            return "boolean";
        }
        return "double".equals(type) || "float".equals(type) || "long".equals(type) ? "number" : type;
    }

    private static void fail(String path, String reason) {
        throw new IntegrationDefaultValueInvalidException(path, reason);
    }
}
