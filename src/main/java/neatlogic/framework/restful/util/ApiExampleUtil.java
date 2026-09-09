package neatlogic.framework.restful.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import neatlogic.framework.exception.type.ApiExampleInvalidException;
import neatlogic.framework.restful.annotation.Example;
import neatlogic.framework.restful.dto.ApiExampleVo;
import neatlogic.framework.util.$;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/** 合并注解与程序化声明，为 API 页面和 MCP 提供同一份场景示例。 */
public final class ApiExampleUtil {
    private static final Logger logger = LoggerFactory.getLogger(ApiExampleUtil.class);

    private ApiExampleUtil() {
    }

    /** 读取注解示例；无注解时返回空列表。 */
    public static JSONArray getExamples(Method method) {
        return getExamples(method, Collections.emptyList(), key -> $.t(key));
    }

    /** 注解按声明顺序在前，方法列表在后；不按标题去重，避免丢失不同场景。 */
    public static JSONArray getExamples(Method method, List<ApiExampleVo> examples) {
        return getExamples(method, examples, key -> $.t(key));
    }

    /** 显式传入翻译器，便于验证语言切换不会污染请求内容。 */
    static JSONArray getExamples(Method method, Function<String, String> translator) {
        return getExamples(method, Collections.emptyList(), translator);
    }

    /** 统一校验并复制每项请求，防止共享对象生成 $ref 或污染原始声明。 */
    static JSONArray getExamples(Method method, List<ApiExampleVo> examples, Function<String, String> translator) {
        JSONArray result = new JSONArray();
        for (Example annotation : method.getAnnotationsByType(Example.class)) {
            Object value;
            try {
                value = JSON.parse(annotation.example(), Feature.OrderedField);
            } catch (Exception ex) {
                logger.error("Invalid API example, method: {}, title: {}", method, annotation.title(), ex);
                throw new ApiExampleInvalidException(method.toGenericString(), annotation.title(), ex);
            }
            result.add(toExample(method, annotation.title(), annotation.description(), value, translator));
        }
        if (examples != null) {
            for (int index = 0; index < examples.size(); index++) {
                ApiExampleVo example = examples.get(index);
                if (example == null) {
                    logger.error("Null API example, method: {}, scenario index: {}", method, index);
                    throw new ApiExampleInvalidException(method.toGenericString(), "example()[" + index + "]", null);
                }
                result.add(toExample(method, example.getTitle(), example.getDescription(), example.getExample(), translator));
            }
        }
        return result;
    }

    /** 只翻译标题与描述；请求必须为对象或数组，缺失标题时明确报告声明错误。 */
    private static JSONObject toExample(Method method, String title, String description, Object value,
                                        Function<String, String> translator) {
        if (StringUtils.isBlank(title) || (!(value instanceof JSONObject) && !(value instanceof JSONArray))) {
            logger.error("API example requires a title and an object or array, method: {}, title: {}", method, title);
            throw new ApiExampleInvalidException(method.toGenericString(), title, null);
        }
        Object copy;
        try {
            copy = JSON.parse(JSON.toJSONString(value, com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect), Feature.OrderedField);
        } catch (Exception ex) {
            logger.error("Cannot copy API example, method: {}, title: {}", method, title, ex);
            throw new ApiExampleInvalidException(method.toGenericString(), title, ex);
        }
        JSONObject item = new JSONObject(true);
        item.put("title", translator.apply(title));
        item.put("description", StringUtils.isBlank(description) ? "" : translator.apply(description));
        item.put("example", copy);
        return item;
    }
}
