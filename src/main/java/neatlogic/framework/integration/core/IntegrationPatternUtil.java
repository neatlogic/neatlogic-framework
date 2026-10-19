package neatlogic.framework.integration.core;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.integration.dto.PatternVo;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 统一提供查询、帮助和运行校验使用的完整参数定义，不向集成配置写入固定参数。 */
public final class IntegrationPatternUtil {
    private IntegrationPatternUtil() {
    }

    public static List<PatternVo> merge(IIntegrationHandler handler, JSONObject config) {
        return merge(handler, config, null);
    }

    /** 固定定义在前，自定义定义在后；同方向同名保留首条，不依赖旧规范开关。 */
    public static List<PatternVo> merge(IIntegrationHandler handler, JSONObject config, String mode) {
        Map<List<String>, PatternVo> patterns = new LinkedHashMap<>();
        JSONObject param = getParamConfig(config);
        if (handler != null) {
            addPatterns(patterns, handler.getInputPattern(), mode, handler.getHandler(), param);
            addPatterns(patterns, handler.getOutputPattern(), mode, handler.getHandler(), param);
        }
        JSONArray customPatterns = param == null ? null : param.getJSONArray("paramList");
        if (customPatterns != null) {
            for (int i = 0; i < customPatterns.size(); i++) {
                // 转为新的定义对象，保留 children，避免改动持久化 JSON 中的自定义参数。
                JSONObject customPattern = customPatterns.getJSONObject(i);
                if (customPattern != null) {
                    addPattern(patterns, copyPattern(customPattern.toJavaObject(PatternVo.class)), mode);
                }
            }
        }
        return new ArrayList<>(patterns.values());
    }

    /** 旧页面将未配置参数保存为空数组；仅对象配置参与合并和运行校验。 */
    static JSONObject getParamConfig(JSONObject config) {
        return config != null && config.get("param") instanceof Map ? config.getJSONObject("param") : null;
    }

    private static void addPatterns(Map<List<String>, PatternVo> patterns, List<PatternVo> source, String mode,
                                    String handler, JSONObject param) {
        if (source != null) {
            for (PatternVo pattern : source) {
                if (pattern != null) {
                    // 固定定义先复制，再关联当前处理器的配置，不能污染处理器单例及其他集成。
                    PatternVo copy = copyPattern(pattern);
                    copy.setDefaultConfig(findBuiltinDefault(param, handler, pattern));
                    addPattern(patterns, copy, mode);
                }
            }
        }
    }

    private static PatternVo copyPattern(PatternVo pattern) {
        return new PatternVo(pattern);
    }

    private static JSONObject findBuiltinDefault(JSONObject param, String handler, PatternVo pattern) {
        JSONArray defaults = param == null ? null : param.getJSONArray("builtinDefaultList");
        if (defaults != null) {
            for (int i = 0; i < defaults.size(); i++) {
                JSONObject entry = defaults.getJSONObject(i);
                if (entry != null && StringUtils.equals(handler, entry.getString("handler"))
                        && StringUtils.equals(pattern.getMode(), entry.getString("mode"))
                        && StringUtils.equals(pattern.getName(), entry.getString("name"))) {
                    return (JSONObject) IntegrationDefaultValueUtil.copyJson(entry.getJSONObject("defaultConfig"));
                }
            }
        }
        return null;
    }

    private static void addPattern(Map<List<String>, PatternVo> patterns, PatternVo pattern, String mode) {
        if (pattern != null && (StringUtils.isBlank(mode) || mode.equals(pattern.getMode()))) {
            // 使用二元键，避免名称中的分隔符冲突；同名输入和输出仍是两个独立参数。
            patterns.putIfAbsent(Arrays.asList(pattern.getMode(), pattern.getName()), pattern);
        }
    }
}
