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

package neatlogic.framework.util.javascript;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 通用 GraalJS 工具的安全、诊断、序列化和缓存测试。 */
public class GraalJsUtilTest {

    @Test
    public void shouldReturnJsonRoundTripObject() {
        Map<String, Object> input = new HashMap<>();
        input.put("enabled", true);

        JSONObject bindingObject = new JSONObject();
        bindingObject.put("input", input);
        Object result = GraalJsUtil.runScript(bindingObject, "return {success: input.enabled, count: 2};");

        Assert.assertTrue(result instanceof Map);
        Assert.assertEquals(Boolean.TRUE, ((Map<?, ?>) result).get("success"));
        Assert.assertEquals(2, ((Number) ((Map<?, ?>) result).get("count")).intValue());
        Assert.assertEquals(Boolean.TRUE, GraalJsUtil.runScript(null, "return this === undefined;"));
    }

    @Test
    public void shouldReturnSupportedValuesAndDiscardConsoleWithoutListener() {
        Assert.assertEquals(Boolean.TRUE, GraalJsUtil.runScript(null, "console.log('discarded'); return true;"));
        Assert.assertEquals("value", GraalJsUtil.runScript(null, "return 'value';"));
        Assert.assertEquals(2, ((Number) GraalJsUtil.runScript(null, "return 2;")).intValue());
        Assert.assertTrue(GraalJsUtil.runScript(null, "return [1, 2];") instanceof JSONArray);
        Assert.assertNull(GraalJsUtil.runScript(null, "return null;"));
        Assert.assertNull(GraalJsUtil.runScript(null, "return undefined;"));
    }

    @Test
    public void shouldNormalizeSyntaxAndRuntimeLocations() {
        try {
            GraalJsUtil.validateScript("const enabled = true;\nreturn enabled && ;");
            Assert.fail("预期返回语法异常");
        } catch (GraalJsException ex) {
            Assert.assertEquals(GraalJsErrorType.SYNTAX_ERROR, ex.getErrorType());
            Assert.assertEquals(Integer.valueOf(2), ex.getStartLine());
            Assert.assertEquals("return enabled && ;", ex.getSourceSnippet());
        }

        try {
            GraalJsUtil.runScript(null, "const enabled = true;\nreturn missing.enabled;");
            Assert.fail("预期返回运行时异常");
        } catch (GraalJsException ex) {
            Assert.assertEquals(GraalJsErrorType.RUNTIME_ERROR, ex.getErrorType());
            Assert.assertEquals(Integer.valueOf(2), ex.getStartLine());
            Assert.assertTrue(ex.getScriptStack().contains("graal-script.js:2"));
        }
    }

    @Test
    public void shouldClampWrapperTailErrorToLastScriptLine() {
        try {
            GraalJsUtil.validateScript("if (true) {");
            Assert.fail("预期返回包装尾部语法异常");
        } catch (GraalJsException ex) {
            Assert.assertEquals(GraalJsErrorType.SYNTAX_ERROR, ex.getErrorType());
            Assert.assertEquals(Integer.valueOf(1), ex.getStartLine());
            Assert.assertEquals("if (true) {", ex.getSourceSnippet());
            Assert.assertFalse(ex.getMessage().contains("graal-script.js:2"));
        }
    }

    @Test
    public void shouldRejectHostAccessAndNonJsonResult() {
        try {
            GraalJsUtil.runScript(null, "return Java.type('java.lang.System');");
            Assert.fail("预期拒绝宿主类访问");
        } catch (GraalJsException ex) {
            Assert.assertEquals(GraalJsErrorType.RUNTIME_ERROR, ex.getErrorType());
        }

        try {
            GraalJsUtil.runScript(null, "const value = {}; value.self = value; return value;");
            Assert.fail("预期返回序列化异常");
        } catch (GraalJsException ex) {
            Assert.assertEquals(GraalJsErrorType.RESULT_SERIALIZATION_ERROR, ex.getErrorType());
        }
    }

    @Test
    public void shouldReturnNullForUndefinedAndRejectFunctionOrSymbol() {
        Assert.assertNull(GraalJsUtil.runScript(null, "return undefined;"));

        assertSerializationError("return function() {}; ");
        assertSerializationError("return Symbol('value');");
    }

    @Test
    public void shouldClassifyBindingFailure() {
        JSONObject bindingObject = new JSONObject();
        bindingObject.put("invalid-name", true);

        try {
            GraalJsUtil.runScript(bindingObject, "return true;");
            Assert.fail("预期返回绑定异常");
        } catch (GraalJsException ex) {
            Assert.assertEquals(GraalJsErrorType.INVALID_BINDING, ex.getErrorType());
        }
    }

    @Test
    public void shouldForceCloseOnTimeout() {
        long startTime = System.currentTimeMillis();

        try {
            GraalJsUtil.runScript(null, "while (true) {}", 1, null);
            Assert.fail("预期返回超时异常");
        } catch (GraalJsException ex) {
            Assert.assertEquals(GraalJsErrorType.TIMEOUT, ex.getErrorType());
            Assert.assertTrue(System.currentTimeMillis() - startTime < 5000L);
        }
    }

    @Test
    public void shouldClassifyTimeoutDuringResultSerialization() {
        try {
            GraalJsUtil.runScript(
                    null,
                    "return {toJSON: function() { while (true) {} }};",
                    1
            );
            Assert.fail("预期返回序列化阶段超时异常");
        } catch (GraalJsException ex) {
            Assert.assertEquals(GraalJsErrorType.TIMEOUT, ex.getErrorType());
        }
    }

    @Test
    public void shouldDeliverLeveledConsoleAndTruncation() {
        List<String> logList = new ArrayList<>();
        boolean[] truncated = new boolean[]{false};
        IGraalJsConsoleListener listener = new IGraalJsConsoleListener() {
            @Override
            public void onConsole(String level, String message) {
                logList.add(level + ":" + message);
            }

            @Override
            public void onTruncated() {
                truncated[0] = true;
            }
        };

        Object result = GraalJsUtil.runScript(
                null,
                "console.warn('ready'); for (let i = 0; i < 105; i++) { console.log(i); } return true;",
                3,
                listener
        );

        Assert.assertEquals(Boolean.TRUE, result);
        Assert.assertEquals(100, logList.size());
        Assert.assertEquals("warn:ready", logList.get(0));
        Assert.assertTrue(truncated[0]);
    }

    @Test
    public void shouldKeepSourceCacheBounded() throws Exception {
        for (int i = 0; i < 120; i++) {
            GraalJsUtil.validateScript("return " + i + ";");
        }

        Field field = GraalJsUtil.class.getDeclaredField("SOURCE_CACHE");
        field.setAccessible(true);
        Map<?, ?> sourceCache = (Map<?, ?>) field.get(null);
        Assert.assertTrue(sourceCache.size() <= 100);
        for (Object cacheKey : sourceCache.keySet()) {
            Assert.assertTrue(cacheKey.toString().startsWith("(function () { 'use strict';\n"));
        }
    }

    private void assertSerializationError(String script) {
        try {
            GraalJsUtil.runScript(null, script);
            Assert.fail("预期返回序列化异常");
        } catch (GraalJsException ex) {
            Assert.assertEquals(GraalJsErrorType.RESULT_SERIALIZATION_ERROR, ex.getErrorType());
        }
    }
}
