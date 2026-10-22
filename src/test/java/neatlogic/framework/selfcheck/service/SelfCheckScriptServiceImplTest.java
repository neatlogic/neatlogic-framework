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

package neatlogic.framework.selfcheck.service;

import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.selfcheck.constvalue.SelfCheckScriptStatus;
import neatlogic.framework.selfcheck.dto.SelfCheckScriptContextVo;
import neatlogic.framework.selfcheck.dto.SelfCheckScriptResultVo;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;

/** 自检脚本适配层的限制、脱敏和返回契约测试。 */
public class SelfCheckScriptServiceImplTest {
    private static final SelfCheckScriptServiceImpl SERVICE = new SelfCheckScriptServiceImpl();

    @AfterClass
    public static void destroyService() {
        SERVICE.destroy();
    }

    @Test
    public void shouldExecuteBooleanResultAndCaptureConsole() {
        SelfCheckScriptContextVo contextVo = new SelfCheckScriptContextVo();
        JSONObject response = new JSONObject();
        response.put("status", "OK");
        contextVo.setResponse(response);
        String script = "console.log('status', context.response.status);\n"
                + "return context.response.status === 'OK';";

        SelfCheckScriptResultVo resultVo = SERVICE.execute(script, contextVo, 3);

        Assert.assertEquals(SelfCheckScriptStatus.SUCCEED.name(), resultVo.getStatus());
        Assert.assertEquals(Boolean.TRUE, resultVo.getSuccess());
        Assert.assertEquals(Boolean.TRUE, resultVo.getRawResult());
        Assert.assertEquals(Boolean.TRUE, resultVo.getValid());
        Assert.assertEquals(1, resultVo.getConsoleList().size());
        Assert.assertEquals("status OK", resultVo.getConsoleList().get(0).getMessage());
    }

    @Test
    public void shouldSupportFalseBooleanResult() {
        SelfCheckScriptResultVo resultVo = SERVICE.execute("return false;", new SelfCheckScriptContextVo(), 3);

        Assert.assertEquals(SelfCheckScriptStatus.ASSERT_FAILED.name(), resultVo.getStatus());
        Assert.assertEquals(Boolean.FALSE, resultVo.getSuccess());
        Assert.assertEquals(Boolean.FALSE, resultVo.getRawResult());
    }

    @Test
    public void shouldReturnSyntaxDiagnosticWithNormalizedLocation() {
        String script = "const value = true;\nreturn value && ;";

        SelfCheckScriptResultVo resultVo = SERVICE.validate(script);

        Assert.assertEquals(SelfCheckScriptStatus.SCRIPT_SYNTAX_ERROR.name(), resultVo.getStatus());
        Assert.assertEquals(Boolean.FALSE, resultVo.getValid());
        Assert.assertFalse(resultVo.getDiagnosticList().isEmpty());
        Assert.assertEquals(Integer.valueOf(2), resultVo.getDiagnosticList().get(0).getStartLine());
        Assert.assertEquals("return value && ;", resultVo.getDiagnosticList().get(0).getSourceSnippet());
    }

    @Test
    public void shouldReturnRuntimeDiagnostic() {
        String script = "return missingValue.enabled;";

        SelfCheckScriptResultVo resultVo = SERVICE.execute(script, new SelfCheckScriptContextVo(), 3);

        Assert.assertEquals(SelfCheckScriptStatus.SCRIPT_RUNTIME_ERROR.name(), resultVo.getStatus());
        Assert.assertEquals(Boolean.TRUE, resultVo.getValid());
        Assert.assertFalse(resultVo.getDiagnosticList().isEmpty());
        Assert.assertEquals("ReferenceError", resultVo.getDiagnosticList().get(0).getErrorType());
        Assert.assertEquals(Integer.valueOf(1), resultVo.getDiagnosticList().get(0).getStartLine());
    }

    @Test
    public void shouldRejectStructuredAndStringResult() {
        SelfCheckScriptResultVo objectResultVo = SERVICE.execute(
                "return {success: true};",
                new SelfCheckScriptContextVo(),
                3
        );
        SelfCheckScriptResultVo stringResultVo = SERVICE.execute(
                "return 'yes';",
                new SelfCheckScriptContextVo(),
                3
        );

        Assert.assertEquals(SelfCheckScriptStatus.SCRIPT_RESULT_ERROR.name(), objectResultVo.getStatus());
        Assert.assertEquals(SelfCheckScriptStatus.SCRIPT_RESULT_ERROR.name(), stringResultVo.getStatus());
    }

    @Test
    public void shouldBlockHostFileAndWorkerAccess() {
        assertRuntimeError("Java.type('java.lang.System'); return true;");
        assertRuntimeError("load('/etc/passwd'); return true;");
        assertRuntimeError("new Worker('worker.js'); return true;");
    }

    @Test
    public void shouldNotExposePromiseOrTimer() {
        String script = "return typeof Promise === 'undefined' && typeof setTimeout === 'undefined';";

        SelfCheckScriptResultVo resultVo = SERVICE.execute(script, new SelfCheckScriptContextVo(), 3);

        Assert.assertEquals(SelfCheckScriptStatus.SUCCEED.name(), resultVo.getStatus());
        Assert.assertEquals(Boolean.TRUE, resultVo.getSuccess());
    }

    @Test
    public void shouldForceCloseInfiniteLoop() {
        long startTime = System.currentTimeMillis();

        SelfCheckScriptResultVo resultVo = SERVICE.execute("while (true) {}", new SelfCheckScriptContextVo(), 1);

        Assert.assertEquals(SelfCheckScriptStatus.SCRIPT_TIMEOUT.name(), resultVo.getStatus());
        Assert.assertTrue(System.currentTimeMillis() - startTime < 5000L);
    }

    @Test
    public void shouldIsolateGlobalStateBetweenExecutions() {
        String script = "globalThis.counter = (globalThis.counter || 0) + 1; return globalThis.counter === 1;";

        SelfCheckScriptResultVo firstResultVo = SERVICE.execute(script, new SelfCheckScriptContextVo(), 3);
        SelfCheckScriptResultVo secondResultVo = SERVICE.execute(script, new SelfCheckScriptContextVo(), 3);

        Assert.assertEquals(SelfCheckScriptStatus.SUCCEED.name(), firstResultVo.getStatus());
        Assert.assertEquals(SelfCheckScriptStatus.SUCCEED.name(), secondResultVo.getStatus());
    }

    @Test
    public void shouldTruncateConsoleAndMaskKnownSensitiveValue() {
        SelfCheckScriptContextVo contextVo = new SelfCheckScriptContextVo();
        contextVo.setSensitiveValueList(Collections.singletonList("key"));
        String script = "for (var i = 0; i < 105; i++) { console.log('key', i); } return true;";

        SelfCheckScriptResultVo resultVo = SERVICE.execute(script, contextVo, 3);

        Assert.assertEquals(SelfCheckScriptStatus.SUCCEED.name(), resultVo.getStatus());
        Assert.assertEquals(100, resultVo.getConsoleList().size());
        Assert.assertEquals(Boolean.TRUE, resultVo.getLogTruncated());
        Assert.assertFalse(resultVo.getConsoleList().get(0).getMessage().contains("key"));
    }

    private void assertRuntimeError(String script) {
        SelfCheckScriptResultVo resultVo = SERVICE.execute(script, new SelfCheckScriptContextVo(), 3);
        Assert.assertEquals(SelfCheckScriptStatus.SCRIPT_RUNTIME_ERROR.name(), resultVo.getStatus());
        Assert.assertFalse(resultVo.getDiagnosticList().isEmpty());
    }
}
