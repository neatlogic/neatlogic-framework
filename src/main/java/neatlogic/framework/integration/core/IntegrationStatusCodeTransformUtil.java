/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the Sustainable Use License (SUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.integration.core;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.JsonNodeFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import neatlogic.framework.exception.core.ApiRuntimeException;
import neatlogic.framework.integration.dto.IntegrationResultVo;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** 从第三方原始 JSON 响应取出状态码，在输出转换之前统一集成的成功和失败判断。 */
public final class IntegrationStatusCodeTransformUtil {
    private static final String CONFIG = "statusCodeTransform";
    private static final String I18N = "nfic.integrationstatuscodetransformutil.";
    private static final Pattern STATUS_CODE = Pattern.compile("[245][0-9]{2}");
    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
            .disable(JsonNodeFeature.STRIP_TRAILING_BIGDECIMAL_ZEROES)
            .build();

    private IntegrationStatusCodeTransformUtil() {
    }

    /** 保存、导入和测试共用同一校验；缺少配置节点的历史集成直接放行。 */
    public static void validate(IIntegrationHandler handler, JSONObject config) {
        if (config == null || !config.containsKey(CONFIG)) {
            return;
        }
        if (handler == null || !handler.supportsStatusCodeTransform()) {
            throw new ApiRuntimeException(I18N + "unsupported");
        }
        if (!(config.get(CONFIG) instanceof Map)) {
            throw new ApiRuntimeException(I18N + "invalidconfig");
        }
        JSONObject rule = config.getJSONObject(CONFIG);
        if (!(rule.get("path") instanceof String)) {
            throw new ApiRuntimeException(I18N + "invalidpath");
        }
        for (String key : rule.getString("path").split("\\.", -1)) {
            if (StringUtils.isBlank(key)) {
                throw new ApiRuntimeException(I18N + "invalidpath");
            }
        }
        if ("map".equals(rule.get("mode"))) {
            validateMappings(rule.get("mappings"));
        } else if (!"direct".equals(rule.get("mode"))) {
            throw new ApiRuntimeException(I18N + "invalidmode");
        }
        if ("error".equals(rule.get("fallback"))) {
            Integer code = parseStatusCode(rule.get("fallbackStatusCode"));
            if (code == null || code < 400) {
                throw new ApiRuntimeException(I18N + "invalidfallbackcode");
            }
        } else if (!"keep".equals(rule.get("fallback"))) {
            throw new ApiRuntimeException(I18N + "invalidfallback");
        }
    }

    /** 将数字和字符串按文本比较，保留字符串前导零并拒绝重复映射。 */
    private static void validateMappings(Object mappings) {
        if (!(mappings instanceof List) || ((List<?>) mappings).isEmpty()) {
            throw new ApiRuntimeException(I18N + "emptymappings");
        }
        Set<String> seen = new HashSet<>();
        for (Object item : (List<?>) mappings) {
            if (!(item instanceof Map)) {
                throw new ApiRuntimeException(I18N + "invalidmapping");
            }
            Map<?, ?> entry = (Map<?, ?>) item;
            Object value = entry.get("value");
            if (!(value instanceof Number || value instanceof String) || StringUtils.isBlank(value.toString())) {
                throw new ApiRuntimeException(I18N + "invalidmapping");
            }
            if (!seen.add(value.toString())) {
                throw new ApiRuntimeException(I18N + "duplicatemapping", value);
            }
            if (parseStatusCode(entry.get("statusCode")) == null) {
                throw new ApiRuntimeException(I18N + "invalidtarget", value);
            }
        }
    }

    /** 仅接受成功码和错误码的三位整数文本，避免小数截断或前导零改写。 */
    private static Integer parseStatusCode(Object value) {
        if ((value instanceof String || value instanceof Number) && STATUS_CODE.matcher(value.toString()).matches()) {
            return Integer.valueOf(value.toString());
        }
        return null;
    }

    /** 调用方在完整读取成功的 2xx 响应后执行；保留原始正文用于测试和审计。 */
    public static void apply(JSONObject config, IntegrationResultVo result) {
        if (config == null || !config.containsKey(CONFIG)) {
            return;
        }
        JSONObject rule = config.getJSONObject(CONFIG);
        String body = result.getRawResult();
        if (StringUtils.isBlank(body)) {
            applyFallback(rule, result, $.t(I18N + "emptybody"));
            return;
        }
        JsonNode node;
        try {
            node = MAPPER.readTree(body);
        } catch (JsonProcessingException ex) {
            applyFallback(rule, result, $.t(I18N + "invalidjson"));
            return;
        }
        String path = rule.getString("path");
        for (String key : path.split("\\.")) {
            node = node.path(key);
        }
        if (node.isMissingNode() || node.isNull()) {
            applyFallback(rule, result, $.t(I18N + "missingvalue"));
            return;
        }
        if (!node.isTextual() && !node.isNumber()) {
            applyFallback(rule, result, $.t(I18N + "invalidtype", node.getNodeType().name()));
            return;
        }
        String value = node.asText();
        Integer code;
        if ("map".equals(rule.getString("mode"))) {
            code = mappedCode(rule, value);
            if (code == null) {
                applyFallback(rule, result, $.t(I18N + "unmapped", value));
                return;
            }
        } else {
            code = parseStatusCode(value);
            if (code == null || node.isFloatingPointNumber()) {
                applyFallback(rule, result, $.t(I18N + "invalidvalue", value));
                return;
            }
        }
        setResult(result, code, $.t(I18N + "matched", path, value, result.getStatusCode(), code));
    }

    /** 按文本精确匹配单条业务码映射。 */
    private static Integer mappedCode(JSONObject rule, String value) {
        for (Object item : rule.getJSONArray("mappings")) {
            Map<?, ?> entry = (Map<?, ?>) item;
            if (value.equals(String.valueOf(entry.get("value")))) {
                return parseStatusCode(entry.get("statusCode"));
            }
        }
        return null;
    }

    /** 即使保留原码也保留兜底原因，便于管理员在测试页确认行为。 */
    private static void applyFallback(JSONObject rule, IntegrationResultVo result, String reason) {
        int rawCode = result.getStatusCode();
        int code = rawCode;
        if ("error".equals(rule.getString("fallback"))) {
            code = rule.getIntValue("fallbackStatusCode");
        }
        setResult(result, code, $.t(I18N + "fallbackdetail", rule.getString("path"), reason, rawCode, code));
    }

    /** 统一生效状态和错误信息，同时保留独立诊断供测试和审计读取。 */
    private static void setResult(IntegrationResultVo result, int code, String detail) {
        result.setStatusCode(code);
        result.setStatusCodeTransformDetail(detail);
        if (code >= 400) {
            result.appendError(detail);
        }
    }
}
