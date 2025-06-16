/*
 * Copyright (C) 2025  深圳极向量科技有限公司 All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neatlogic.framework.util;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AviatorEvaluatorUtil {
    private static final Logger logger = LoggerFactory.getLogger(AviatorEvaluatorUtil.class);
    // 缓存编译后的表达式
    private static final ConcurrentHashMap<String, Expression> expressionCache = new ConcurrentHashMap<>();

    // 匹配 ${...} 的变量表达式
    private static final Pattern VAR_PATTERN = Pattern.compile("\\$\\{([^}]+)}");

    /**
     * 处理表达式变量（将 ${DATA.env} 替换成 DATA.env）
     */
    private static String normalizeExpression(String expr) {
        Matcher matcher = VAR_PATTERN.matcher(expr);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 执行 Aviator 表达式
     *
     * @param rawExpression 表达式，支持 ${var} 格式
     * @param variables     变量上下文 Map（支持嵌套）
     * @return 结果（Boolean、String、Number 等）
     */
    public static Object evaluate(String rawExpression, Map<String, Object> variables) {
        try {
            String finalExpression = normalizeExpression(rawExpression);
            Expression compiled = expressionCache.computeIfAbsent(finalExpression, AviatorEvaluator::compile);
            return compiled.execute(variables);
        } catch (Exception e) {
            // 可替换为日志系统
            logger.error("Aviator expression failed: " + rawExpression + ",errMsg:" + e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行 Aviator 表达式
     *
     * @param finalExpression 最终表达式
     * @return 结果（Boolean、String、Number 等）
     */
    public static Object evaluate(String finalExpression) {
        try {
            Expression compiled = expressionCache.computeIfAbsent(finalExpression, AviatorEvaluator::compile);
            return compiled.execute();
        } catch (Exception e) {
            // 可替换为日志系统
            logger.error("Aviator expression failed: " + finalExpression + ",errMsg:" + e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 执行布尔类型表达式，返回 true/false
     */
    public static boolean evaluateBoolean(String rawExpression, Map<String, Object> variables) {
        Object result = evaluate(rawExpression, variables);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        if (result == null) return false;
        return Boolean.parseBoolean(result.toString());
    }


    /**
     * 执行布尔类型表达式，返回 true/false
     */
    public static boolean evaluateBoolean(String finalExpression) {
        Object result = evaluate(finalExpression);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        if (result == null) return false;
        return Boolean.parseBoolean(result.toString());
    }

}
