/*
 *
 * Copyright (C) 2025  TechSure Co., Ltd.  All Rights Reserved.
 * This file is part of the NeatLogic software.
 * Licensed under the NeatLogic Sustainable Use License (NSUL), Version 4.x – 2025.
 * You may use this file only in compliance with the License.
 * See the LICENSE file distributed with this work for the full license text.
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 */

package neatlogic.framework.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AviatorEvaluatorUtil {

    private static final Logger logger = LoggerFactory.getLogger(AviatorEvaluatorUtil.class);

    private static final Pattern VAR_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    // Guava 缓存表达式：最多缓存 1000 条，1 小时内未访问会被清理
    private static final LoadingCache<String, Expression> expressionCache =
            CacheBuilder.newBuilder()
                    .maximumSize(1000)
                    .expireAfterAccess(1, TimeUnit.HOURS)
                    .build(new CacheLoader<String, Expression>() {
                        @Override
                        public Expression load(String key) throws Exception {
                            return AviatorEvaluator.compile(key, true);
                        }
                    });

    /**
     * 将 ${env-var} 转换为 env_var
     */
    private static String normalizeExpression(String expr) {
        Matcher matcher = VAR_PATTERN.matcher(expr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replacement = matcher.group(1).replace("-", "_");
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 替换变量中的非法 key，避免 Aviator 不识别
     */
    private static Map<String, Object> getFinalVariables(Map<String, Object> variables) {
        if (MapUtils.isEmpty(variables)) {
            return variables;
        }
        Map<String, Object> finalVariables = new HashMap<>();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String safeKey = entry.getKey().replace("-", "_");
            if (finalVariables.containsKey(safeKey)) {
                logger.warn("Variable name conflict: multiple keys map to {}", safeKey);
            }
            finalVariables.put(safeKey, entry.getValue());
        }
        return finalVariables;
    }

    /**
     * 通用表达式执行（含变量）
     */
    public static Object evaluate(String rawExpression, Map<String, Object> variables) {
        try {
            String normalizedExpr = normalizeExpression(rawExpression);
            Expression expression = expressionCache.get(normalizedExpr);
            return expression.execute(getFinalVariables(variables));
        } catch (Exception e) {
            logger.error("Aviator expression failed: {}, error: {}", rawExpression, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 执行表达式（无变量）
     */
    public static Object evaluate(String rawExpression) {
        try {
            Expression expression = expressionCache.get(rawExpression);
            return expression.execute();
        } catch (Exception e) {
            logger.error("Aviator expression failed: {}, error: {}", rawExpression, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 执行布尔表达式（含变量）
     */
    public static boolean evaluateBoolean(String rawExpression, Map<String, Object> variables) {
        Object result = evaluate(rawExpression, variables);
        return toBoolean(result, rawExpression);
    }

    /**
     * 执行布尔表达式（无变量）
     */
    public static boolean evaluateBoolean(String rawExpression) {
        Object result = evaluate(rawExpression);
        return toBoolean(result, rawExpression);
    }

    private static boolean toBoolean(Object result, String context) {
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        if (result == null) {
            logger.warn("Boolean evaluation returned null: {}", context);
            return false;
        }
        boolean parsed = Boolean.parseBoolean(result.toString());
        logger.debug("Parsed boolean [{}] from expression: {}", parsed, context);
        return parsed;
    }
}
