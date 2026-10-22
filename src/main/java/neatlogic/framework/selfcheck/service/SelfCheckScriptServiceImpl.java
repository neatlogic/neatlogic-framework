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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import neatlogic.framework.selfcheck.constvalue.SelfCheckScriptStatus;
import neatlogic.framework.selfcheck.dto.SelfCheckScriptContextVo;
import neatlogic.framework.selfcheck.dto.SelfCheckScriptDiagnosticVo;
import neatlogic.framework.selfcheck.dto.SelfCheckScriptLogVo;
import neatlogic.framework.selfcheck.dto.SelfCheckScriptResultVo;
import neatlogic.framework.util.javascript.GraalJsErrorType;
import neatlogic.framework.util.javascript.GraalJsException;
import neatlogic.framework.util.javascript.GraalJsUtil;
import neatlogic.framework.util.javascript.IGraalJsConsoleListener;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自检脚本业务适配层。
 * 通用沙箱能力由 GraalJsUtil 提供，本服务只负责自检限流、大小限制、脱敏和状态转换。
 */
@Service
public class SelfCheckScriptServiceImpl implements SelfCheckScriptService {
    static final int DEFAULT_TIMEOUT_SECONDS = 3;
    static final int MAX_TIMEOUT_SECONDS = 10;
    static final int MAX_SCRIPT_BYTES = 64 * 1024;
    static final int MAX_CONTEXT_MEMBER_BYTES = 1024 * 1024;
    static final int MAX_CONSOLE_COUNT = 100;
    static final int MAX_CONSOLE_BYTES = 32 * 1024;
    private static final int MAX_MESSAGE_CHARS = 4000;
    private static final int MAX_STACK_CHARS = 8000;
    private static final String PHASE_COMPILE = "SCRIPT_COMPILE";
    private static final String PHASE_EXECUTE = "SCRIPT_EXECUTE";

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            4,
            4,
            60L,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(20),
            runnable -> {
                Thread thread = new Thread(runnable);
                thread.setName("selfcheck-script-" + ThreadSequenceHolder.SEQUENCE.incrementAndGet());
                thread.setDaemon(true);
                return thread;
            },
            new ThreadPoolExecutor.AbortPolicy()
    );

    /** 独立静态持有序号，避免把可变计数器暴露给脚本。 */
    private static final class ThreadSequenceHolder {
        private static final AtomicInteger SEQUENCE = new AtomicInteger();
    }

    @Override
    public SelfCheckScriptResultVo validate(String script) {
        return run(script, null, DEFAULT_TIMEOUT_SECONDS, false);
    }

    @Override
    public SelfCheckScriptResultVo execute(String script, SelfCheckScriptContextVo contextVo, Integer timeoutSeconds) {
        int actualTimeoutSeconds = normalizeTimeout(timeoutSeconds);
        SelfCheckScriptContextVo actualContextVo = contextVo;
        if (actualContextVo == null) {
            actualContextVo = new SelfCheckScriptContextVo();
        }
        return run(script, actualContextVo, actualTimeoutSeconds, true);
    }

    private SelfCheckScriptResultVo run(
            String script,
            SelfCheckScriptContextVo contextVo,
            int timeoutSeconds,
            boolean execute
    ) {
        long startNano = System.nanoTime();
        SelfCheckScriptResultVo lengthCheckResult = checkScriptLength(script);
        if (lengthCheckResult != null) {
            lengthCheckResult.setDurationMs(elapsedMillis(startNano));
            return lengthCheckResult;
        }
        SelfCheckScriptResultVo contextCheckResult = checkContextSize(contextVo);
        if (contextCheckResult != null) {
            contextCheckResult.setDurationMs(elapsedMillis(startNano));
            return contextCheckResult;
        }

        Future<SelfCheckScriptResultVo> future;
        try {
            future = executor.submit(() -> processScript(script, contextVo, timeoutSeconds, execute));
        } catch (RejectedExecutionException ex) {
            SelfCheckScriptResultVo resultVo = createResult(SelfCheckScriptStatus.BUSY, false, false);
            resultVo.setMessage("SELF_CHECK_SCRIPT_EXECUTOR_BUSY");
            resultVo.setDurationMs(elapsedMillis(startNano));
            return resultVo;
        }

        try {
            SelfCheckScriptResultVo resultVo = future.get(timeoutSeconds + 1L, TimeUnit.SECONDS);
            resultVo.setDurationMs(elapsedMillis(startNano));
            return resultVo;
        } catch (TimeoutException ex) {
            future.cancel(true);
            SelfCheckScriptResultVo resultVo = createResult(SelfCheckScriptStatus.SCRIPT_TIMEOUT, false, null);
            resultVo.setMessage("SELF_CHECK_SCRIPT_TIMEOUT");
            resultVo.setDurationMs(elapsedMillis(startNano));
            return resultVo;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            future.cancel(true);
            SelfCheckScriptResultVo resultVo = createResult(SelfCheckScriptStatus.SCRIPT_TIMEOUT, false, null);
            resultVo.setMessage("SELF_CHECK_SCRIPT_INTERRUPTED");
            resultVo.setDurationMs(elapsedMillis(startNano));
            return resultVo;
        } catch (ExecutionException ex) {
            SelfCheckScriptResultVo resultVo = createResult(SelfCheckScriptStatus.SCRIPT_RUNTIME_ERROR, false, null);
            String message = null;
            if (ex.getCause() != null) {
                message = ex.getCause().getMessage();
            }
            resultVo.setMessage(maskAndLimit(message, sensitiveValues(contextVo), MAX_MESSAGE_CHARS));
            resultVo.setDurationMs(elapsedMillis(startNano));
            return resultVo;
        }
    }

    private SelfCheckScriptResultVo processScript(
            String script,
            SelfCheckScriptContextVo contextVo,
            int timeoutSeconds,
            boolean execute
    ) {
        SelfCheckScriptResultVo resultVo = new SelfCheckScriptResultVo();
        try {
            if (!execute) {
                GraalJsUtil.validateScript(script);
                resultVo.setStatus(SelfCheckScriptStatus.SUCCEED.name());
                resultVo.setSuccess(true);
                resultVo.setValid(true);
                return resultVo;
            }

            resultVo.setValid(true);
            IGraalJsConsoleListener consoleListener = createConsoleListener(resultVo, contextVo);
            JSONObject bindingObject = new JSONObject(true);
            bindingObject.put("context", contextVo.toScriptJson());
            Object rawResult = GraalJsUtil.runScript(
                    bindingObject,
                    script,
                    timeoutSeconds,
                    consoleListener
            );
            if (!(rawResult instanceof Boolean)) {
                resultVo.setStatus(SelfCheckScriptStatus.SCRIPT_RESULT_ERROR.name());
                resultVo.setSuccess(false);
                resultVo.setMessage("SELF_CHECK_SCRIPT_RESULT_BOOLEAN_REQUIRED");
                return resultVo;
            }

            Boolean success = (Boolean) rawResult;
            resultVo.setRawResult(success);
            resultVo.setSuccess(success);
            if (Boolean.TRUE.equals(success)) {
                resultVo.setStatus(SelfCheckScriptStatus.SUCCEED.name());
            } else {
                resultVo.setStatus(SelfCheckScriptStatus.ASSERT_FAILED.name());
            }
            return resultVo;
        } catch (GraalJsException ex) {
            populateExceptionResult(resultVo, ex, execute, contextVo);
            return resultVo;
        } catch (Exception ex) {
            resultVo.setStatus(SelfCheckScriptStatus.SCRIPT_RUNTIME_ERROR.name());
            resultVo.setSuccess(false);
            resultVo.setMessage(maskAndLimit(ex.getMessage(), sensitiveValues(contextVo), MAX_MESSAGE_CHARS));
            return resultVo;
        }
    }

    private IGraalJsConsoleListener createConsoleListener(
            SelfCheckScriptResultVo resultVo,
            SelfCheckScriptContextVo contextVo
    ) {
        return new IGraalJsConsoleListener() {
            private int remainingBytes = MAX_CONSOLE_BYTES;

            @Override
            public void onConsole(String level, String message) {
                if (resultVo.getConsoleList().size() >= MAX_CONSOLE_COUNT || remainingBytes <= 0) {
                    resultVo.setLogTruncated(true);
                    return;
                }
                String maskedMessage = maskAndLimit(message, sensitiveValues(contextVo), MAX_CONSOLE_BYTES);
                int messageBytes = maskedMessage.getBytes(StandardCharsets.UTF_8).length;
                if (messageBytes > remainingBytes) {
                    maskedMessage = limitUtf8Bytes(maskedMessage, remainingBytes);
                    messageBytes = maskedMessage.getBytes(StandardCharsets.UTF_8).length;
                    resultVo.setLogTruncated(true);
                }
                SelfCheckScriptLogVo logVo = new SelfCheckScriptLogVo();
                logVo.setLevel(level);
                logVo.setMessage(maskedMessage);
                resultVo.getConsoleList().add(logVo);
                remainingBytes -= messageBytes;
            }

            @Override
            public void onTruncated() {
                resultVo.setLogTruncated(true);
            }
        };
    }

    private void populateExceptionResult(
            SelfCheckScriptResultVo resultVo,
            GraalJsException exception,
            boolean execute,
            SelfCheckScriptContextVo contextVo
    ) {
        GraalJsErrorType errorType = exception.getErrorType();
        if (GraalJsErrorType.SYNTAX_ERROR.equals(errorType)) {
            resultVo.setStatus(SelfCheckScriptStatus.SCRIPT_SYNTAX_ERROR.name());
            resultVo.setValid(false);
        } else if (GraalJsErrorType.TIMEOUT.equals(errorType)) {
            resultVo.setStatus(SelfCheckScriptStatus.SCRIPT_TIMEOUT.name());
            resultVo.setMessage("SELF_CHECK_SCRIPT_TIMEOUT");
        } else if (GraalJsErrorType.RESULT_SERIALIZATION_ERROR.equals(errorType)
                || GraalJsErrorType.INVALID_BINDING.equals(errorType)) {
            resultVo.setStatus(SelfCheckScriptStatus.SCRIPT_RESULT_ERROR.name());
        } else {
            resultVo.setStatus(SelfCheckScriptStatus.SCRIPT_RUNTIME_ERROR.name());
        }
        resultVo.setSuccess(false);
        if (resultVo.getMessage() == null) {
            resultVo.setMessage(maskAndLimit(exception.getMessage(), sensitiveValues(contextVo), MAX_MESSAGE_CHARS));
        }
        if (!GraalJsErrorType.TIMEOUT.equals(errorType)) {
            String phase = PHASE_EXECUTE;
            if (!execute || GraalJsErrorType.SYNTAX_ERROR.equals(errorType)) {
                phase = PHASE_COMPILE;
            }
            resultVo.addDiagnostic(createDiagnostic(exception, phase, contextVo));
        }
    }

    private SelfCheckScriptDiagnosticVo createDiagnostic(
            GraalJsException exception,
            String phase,
            SelfCheckScriptContextVo contextVo
    ) {
        SelfCheckScriptDiagnosticVo diagnosticVo = new SelfCheckScriptDiagnosticVo();
        diagnosticVo.setPhase(phase);
        diagnosticVo.setErrorType(resolveDiagnosticErrorType(exception));
        diagnosticVo.setMessage(maskAndLimit(exception.getMessage(), sensitiveValues(contextVo), MAX_MESSAGE_CHARS));
        diagnosticVo.setStartLine(exception.getStartLine());
        diagnosticVo.setStartColumn(exception.getStartColumn());
        diagnosticVo.setEndLine(exception.getEndLine());
        diagnosticVo.setEndColumn(exception.getEndColumn());
        diagnosticVo.setSourceSnippet(maskAndLimit(exception.getSourceSnippet(), sensitiveValues(contextVo), MAX_MESSAGE_CHARS));
        diagnosticVo.setScriptStack(maskAndLimit(exception.getScriptStack(), sensitiveValues(contextVo), MAX_STACK_CHARS));
        return diagnosticVo;
    }

    private String resolveDiagnosticErrorType(GraalJsException exception) {
        if (GraalJsErrorType.SYNTAX_ERROR.equals(exception.getErrorType())) {
            return "SyntaxError";
        }
        String message = exception.getMessage();
        if (StringUtils.isNotBlank(message)) {
            int delimiterIndex = message.indexOf(':');
            if (delimiterIndex > 0 && delimiterIndex < 80) {
                return message.substring(0, delimiterIndex);
            }
        }
        return exception.getErrorType().name();
    }

    private SelfCheckScriptResultVo checkScriptLength(String script) {
        if (StringUtils.isBlank(script)) {
            SelfCheckScriptResultVo resultVo = createResult(SelfCheckScriptStatus.SCRIPT_RESULT_ERROR, false, false);
            resultVo.setMessage("SELF_CHECK_SCRIPT_REQUIRED");
            return resultVo;
        }
        if (script.getBytes(StandardCharsets.UTF_8).length > MAX_SCRIPT_BYTES) {
            SelfCheckScriptResultVo resultVo = createResult(SelfCheckScriptStatus.SCRIPT_RESULT_ERROR, false, false);
            resultVo.setMessage("SELF_CHECK_SCRIPT_TOO_LARGE");
            return resultVo;
        }
        return null;
    }

    /** 请求和响应分别限制为 1MB，防止复制到 Guest Context 时放大内存占用。 */
    private SelfCheckScriptResultVo checkContextSize(SelfCheckScriptContextVo contextVo) {
        if (contextVo == null) {
            return null;
        }
        try {
            if (jsonBytes(contextVo.getRequest()) > MAX_CONTEXT_MEMBER_BYTES
                    || jsonBytes(contextVo.getResponse()) > MAX_CONTEXT_MEMBER_BYTES) {
                SelfCheckScriptResultVo resultVo = createResult(SelfCheckScriptStatus.SCRIPT_RESULT_ERROR, false, false);
                resultVo.setMessage("SELF_CHECK_SCRIPT_CONTEXT_TOO_LARGE");
                return resultVo;
            }
        } catch (Exception ex) {
            SelfCheckScriptResultVo resultVo = createResult(SelfCheckScriptStatus.SCRIPT_RESULT_ERROR, false, false);
            resultVo.setMessage("SELF_CHECK_SCRIPT_CONTEXT_NOT_JSON");
            return resultVo;
        }
        return null;
    }

    private int jsonBytes(Object value) {
        if (value == null) {
            return 0;
        }
        return JSON.toJSONString(value).getBytes(StandardCharsets.UTF_8).length;
    }

    private SelfCheckScriptResultVo createResult(SelfCheckScriptStatus status, Boolean success, Boolean valid) {
        SelfCheckScriptResultVo resultVo = new SelfCheckScriptResultVo();
        resultVo.setStatus(status.name());
        resultVo.setSuccess(success);
        resultVo.setValid(valid);
        return resultVo;
    }

    private List<String> sensitiveValues(SelfCheckScriptContextVo contextVo) {
        if (contextVo == null || contextVo.getSensitiveValueList() == null) {
            return new ArrayList<>();
        }
        return contextVo.getSensitiveValueList();
    }

    private String maskAndLimit(String source, List<String> sensitiveValueList, int maxChars) {
        if (source == null) {
            return null;
        }
        String result = source;
        if (sensitiveValueList != null) {
            for (String sensitiveValue : sensitiveValueList) {
                if (StringUtils.isNotBlank(sensitiveValue)) {
                    result = result.replace(sensitiveValue, "******");
                }
            }
        }
        return limit(result, maxChars);
    }

    private String limit(String source, int maxChars) {
        if (source == null || source.length() <= maxChars) {
            return source;
        }
        return source.substring(0, maxChars);
    }

    /** 按 UTF-8 字节截断，避免从代理对中间切断 Unicode 字符。 */
    private String limitUtf8Bytes(String source, int maxBytes) {
        if (source == null || source.getBytes(StandardCharsets.UTF_8).length <= maxBytes) {
            return source;
        }
        StringBuilder result = new StringBuilder();
        int usedBytes = 0;
        for (int offset = 0; offset < source.length();) {
            int codePoint = source.codePointAt(offset);
            String codePointText = new String(Character.toChars(codePoint));
            int codePointBytes = codePointText.getBytes(StandardCharsets.UTF_8).length;
            if (usedBytes + codePointBytes > maxBytes) {
                break;
            }
            result.append(codePointText);
            usedBytes += codePointBytes;
            offset += Character.charCount(codePoint);
        }
        return result.toString();
    }

    /** 自检层继续限制为最多 10 秒，避免配置错误长期占用专用执行队列。 */
    private int normalizeTimeout(Integer timeoutSeconds) {
        if (timeoutSeconds == null || timeoutSeconds <= 0) {
            return DEFAULT_TIMEOUT_SECONDS;
        } else if (timeoutSeconds > MAX_TIMEOUT_SECONDS) {
            return MAX_TIMEOUT_SECONDS;
        } else {
            return timeoutSeconds;
        }
    }

    private long elapsedMillis(long startNano) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNano);
    }

    @PreDestroy
    public void destroy() {
        executor.shutdownNow();
    }
}
