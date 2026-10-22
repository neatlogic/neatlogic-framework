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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.IOAccess;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 通用 GraalJS 安全执行工具。
 * 调用方传入的是严格模式函数体，参数和结果都必须经过 JSON 序列化往返。
 */
public final class GraalJsUtil {
    public static final int DEFAULT_TIMEOUT_SECONDS = 3;
    public static final int MAX_TIMEOUT_SECONDS = 60;
    private static final int SOURCE_CACHE_LIMIT = 100;
    private static final int MAX_CONSOLE_COUNT = 100;
    private static final int MAX_CONSOLE_BYTES = 32 * 1024;
    private static final int MAX_STACK_CHARS = 8000;
    private static final long MAX_STATEMENT_COUNT = 10_000_000L;
    private static final String SCRIPT_NAME = "graal-script.js";
    private static final String WRAPPER_PREFIX = "(function () { 'use strict';\n";
    private static final String WRAPPER_SUFFIX = "\n})()";
    private static final String IDENTIFIER_PATTERN = "[A-Za-z_$][A-Za-z0-9_$]*";
    private static final Set<String> RESERVED_IDENTIFIER_SET = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            "await", "break", "case", "catch", "class", "const", "continue", "debugger", "default", "delete",
            "do", "else", "enum", "export", "extends", "false", "finally", "for", "function", "if", "implements",
            "import", "in", "instanceof", "interface", "let", "new", "null", "package", "private", "protected",
            "public", "return", "static", "super", "switch", "this", "throw", "true", "try", "typeof", "var",
            "void", "while", "with", "yield", "arguments", "eval"
    )));
    private static final String DISABLE_UNSAFE_GLOBAL_SCRIPT = "(function(){"
            + "const names=['Promise','setTimeout','setInterval','queueMicrotask','fetch','XMLHttpRequest','WebSocket','Worker','require','load','loadWithNewGlobal'];"
            + "names.forEach(function(name){Object.defineProperty(globalThis,name,{value:undefined,writable:false,configurable:false});});"
            + "})()";
    private static final String CONSOLE_SUPPORT_SCRIPT = "(function(){"
            + "const logs=[];let total=0;let truncated=false;"
            + "function format(value){if(typeof value==='string'){return value;}"
            + "try{const text=JSON.stringify(value);if(text!==undefined){return text;}}catch(e){}"
            + "try{return String(value);}catch(e){return '[Unprintable]';}}"
            + "function write(level,args){if(logs.length>=100){truncated=true;return;}"
            + "let text=Array.prototype.map.call(args,format).join(' ');"
            + "const remain=32768-total;if(remain<=0){truncated=true;return;}"
            + "if(text.length>remain){text=text.substring(0,remain);truncated=true;}"
            + "logs.push({level:level,message:text});total+=text.length;}"
            + "return Object.freeze({console:Object.freeze({"
            + "log:function(){write('log',arguments);},warn:function(){write('warn',arguments);},error:function(){write('error',arguments);}}),"
            + "readLogs:function(){return JSON.stringify(logs);},isTruncated:function(){return truncated;}});"
            + "})()";
    private static final String RESULT_SERIALIZER_SCRIPT = "(function(value){"
            + "const type=typeof value;"
            + "if(type==='undefined'){return JSON.stringify({kind:'undefined'});}"
            + "if(type==='function'||type==='symbol'){throw new TypeError('JavaScript返回值不支持'+type+'类型');}"
            + "return JSON.stringify({kind:'value',value:value});"
            + "})";
    private static final Engine ENGINE = Engine.newBuilder()
            .option("engine.WarnInterpreterOnly", "false")
            .build();
    private static final ScheduledThreadPoolExecutor TIMEOUT_SCHEDULER = createTimeoutScheduler();
    private static final Map<String, Source> SOURCE_CACHE = Collections.synchronizedMap(
            new LinkedHashMap<String, Source>(128, 0.75F, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Source> eldest) {
                    return size() > SOURCE_CACHE_LIMIT;
                }
            }
    );

    static {
        Thread shutdownHook = new Thread(() -> {
            TIMEOUT_SCHEDULER.shutdownNow();
            try {
                ENGINE.close(true);
            } catch (Exception ignored) {
                // JVM 退出时只做资源兜底，不让关闭异常阻断其他 shutdown hook。
            }
        });
        shutdownHook.setName("graaljs-shutdown");
        shutdownHook.setContextClassLoader(null);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
    }

    private GraalJsUtil() {
    }

    /** 仅编译脚本，不执行用户代码。 */
    public static void validateScript(String script) {
        runInternal(script, null, DEFAULT_TIMEOUT_SECONDS, null, false);
    }

    /** 使用默认超时执行脚本。 */
    public static Object runScript(JSONObject paramObject, String script) {
        return runScript(paramObject, script, DEFAULT_TIMEOUT_SECONDS, null);
    }

    /** 使用指定超时执行脚本。 */
    public static Object runScript(JSONObject paramObject, String script, int timeoutSeconds) {
        return runScript(paramObject, script, timeoutSeconds, null);
    }

    /** 执行脚本并返回经过 JSON 往返后的普通 Java 对象。 */
    public static Object runScript(
            JSONObject paramObject,
            String script,
            int timeoutSeconds,
            IGraalJsConsoleListener consoleListener
    ) {
        int actualTimeoutSeconds = normalizeTimeout(timeoutSeconds);
        JSONObject actualBindingObject = paramObject;
        if (actualBindingObject == null) {
            actualBindingObject = new JSONObject(true);
        }
        return runInternal(script, actualBindingObject, actualTimeoutSeconds, consoleListener, true);
    }

    private static Object runInternal(
            String script,
            JSONObject bindingObject,
            int timeoutSeconds,
            IGraalJsConsoleListener consoleListener,
            boolean execute
    ) {
        if (script == null) {
            throw simpleException(GraalJsErrorType.SYNTAX_ERROR, "脚本不能为空", null);
        }
        JSONObject jsonBindingObject = null;
        if (execute) {
            jsonBindingObject = normalizeBindings(bindingObject);
        }
        Source source = source(script);
        Context context = createContext();
        AtomicBoolean finished = new AtomicBoolean(false);
        AtomicBoolean timedOut = new AtomicBoolean(false);
        ScheduledFuture<?> timeoutFuture = TIMEOUT_SCHEDULER.schedule(() -> {
            if (finished.compareAndSet(false, true)) {
                timedOut.set(true);
                try {
                    context.close(true);
                } catch (Exception ignored) {
                    // 执行线程会统一返回超时异常，关闭异常不覆盖业务可理解的诊断。
                }
            }
        }, timeoutSeconds, TimeUnit.SECONDS);
        Value consoleSupport = null;
        try {
            context.parse(source);
            if (!execute) {
                return null;
            }
            installBindings(context, jsonBindingObject);
            context.eval("js", DISABLE_UNSAFE_GLOBAL_SCRIPT);
            if (consoleListener != null) {
                consoleSupport = context.eval("js", CONSOLE_SUPPORT_SCRIPT);
                context.getBindings("js").putMember("console", consoleSupport.getMember("console"));
            }
            Value guestResult = context.eval(source);
            return serializeResult(context, guestResult);
        } catch (PolyglotException ex) {
            if (timedOut.get() || ex.isCancelled() || ex.isInterrupted() || ex.isResourceExhausted()) {
                throw simpleException(GraalJsErrorType.TIMEOUT, "JavaScript 执行超时", ex);
            }
            GraalJsErrorType errorType = GraalJsErrorType.RUNTIME_ERROR;
            if (ex.isSyntaxError()) {
                errorType = GraalJsErrorType.SYNTAX_ERROR;
            }
            throw diagnosticException(errorType, ex, script);
        } finally {
            if (!timedOut.get() && consoleSupport != null && consoleListener != null) {
                publishConsole(consoleSupport, consoleListener);
            }
            finished.set(true);
            timeoutFuture.cancel(false);
            closeContext(context);
        }
    }

    /** 所有绑定先做 JSON 往返，再逐键校验名称，拒绝把 Java 对象直接暴露给脚本。 */
    private static JSONObject normalizeBindings(JSONObject bindingObject) {
        try {
            String bindingJson = JSON.toJSONString(bindingObject);
            JSONObject result = JSON.parseObject(bindingJson);
            for (String bindingName : result.keySet()) {
                validateBindingName(bindingName);
            }
            return result;
        } catch (GraalJsException ex) {
            throw ex;
        } catch (Exception ex) {
            throw simpleException(GraalJsErrorType.INVALID_BINDING, "绑定参数无法转换为 JSON", ex);
        }
    }

    private static void validateBindingName(String bindingName) {
        if (StringUtils.isBlank(bindingName)
                || !bindingName.matches(IDENTIFIER_PATTERN)
                || RESERVED_IDENTIFIER_SET.contains(bindingName)) {
            throw simpleException(GraalJsErrorType.INVALID_BINDING, "非法的 JavaScript 绑定名称: " + bindingName, null);
        }
    }

    /** 在同一 Context 内解析 JSON，并只把 guest value 放入脚本 bindings。 */
    private static void installBindings(Context context, JSONObject bindingObject) {
        try {
            String bindingJson = bindingObject.toJSONString();
            String parseExpression = "JSON.parse(" + JSON.toJSONString(bindingJson) + ")";
            Value guestBindingObject = context.eval("js", parseExpression);
            for (String bindingName : bindingObject.keySet()) {
                context.getBindings("js").putMember(bindingName, guestBindingObject.getMember(bindingName));
            }
        } catch (PolyglotException ex) {
            throw simpleException(GraalJsErrorType.INVALID_BINDING, "绑定参数无法注入 JavaScript 上下文", ex);
        } catch (Exception ex) {
            throw simpleException(GraalJsErrorType.INVALID_BINDING, "绑定参数无法注入 JavaScript 上下文", ex);
        }
    }

    private static Object serializeResult(Context context, Value guestResult) {
        try {
            Value serializer = context.eval("js", RESULT_SERIALIZER_SCRIPT);
            Value serializedValue = serializer.execute(guestResult);
            if (serializedValue == null || serializedValue.isNull() || !serializedValue.isString()) {
                throw simpleException(
                        GraalJsErrorType.RESULT_SERIALIZATION_ERROR,
                        "JavaScript 返回值无法转换为 JSON",
                        null
                );
            }
            JSONObject envelope = JSON.parseObject(serializedValue.asString());
            if ("undefined".equals(envelope.getString("kind"))) {
                return null;
            }
            return envelope.get("value");
        } catch (GraalJsException ex) {
            throw ex;
        } catch (PolyglotException ex) {
            if (ex.isCancelled() || ex.isInterrupted() || ex.isResourceExhausted()) {
                throw ex;
            }
            throw simpleException(GraalJsErrorType.RESULT_SERIALIZATION_ERROR, ex.getMessage(), ex);
        } catch (Exception ex) {
            throw simpleException(
                    GraalJsErrorType.RESULT_SERIALIZATION_ERROR,
                    "JavaScript 返回值无法转换为 JSON",
                    ex
            );
        }
    }

    private static Source source(String script) {
        String wrappedScript = WRAPPER_PREFIX + script + WRAPPER_SUFFIX;
        Source source = SOURCE_CACHE.get(wrappedScript);
        if (source != null) {
            return source;
        }
        try {
            Source newSource = Source.newBuilder("js", wrappedScript, SCRIPT_NAME).buildLiteral();
            synchronized (SOURCE_CACHE) {
                Source cachedSource = SOURCE_CACHE.get(wrappedScript);
                if (cachedSource != null) {
                    return cachedSource;
                }
                SOURCE_CACHE.put(wrappedScript, newSource);
            }
            return newSource;
        } catch (Exception ex) {
            throw simpleException(GraalJsErrorType.SYNTAX_ERROR, "JavaScript 源码构建失败", ex);
        }
    }

    /** 创建完全禁止宿主、IO、进程、线程、环境变量和跨语言访问的上下文。 */
    private static Context createContext() {
        return Context.newBuilder("js")
                .engine(ENGINE)
                .in(InputStream.nullInputStream())
                .out(OutputStream.nullOutputStream())
                .err(OutputStream.nullOutputStream())
                .allowAllAccess(false)
                .allowHostAccess(HostAccess.NONE)
                .allowHostClassLookup(className -> false)
                .allowHostClassLoading(false)
                .allowIO(IOAccess.NONE)
                .allowNativeAccess(false)
                .allowCreateThread(false)
                .allowCreateProcess(false)
                .allowEnvironmentAccess(EnvironmentAccess.NONE)
                .allowPolyglotAccess(PolyglotAccess.NONE)
                .allowValueSharing(false)
                .allowInnerContextOptions(false)
                .useSystemExit(false)
                .resourceLimits(ResourceLimits.newBuilder().statementLimit(MAX_STATEMENT_COUNT, source -> true).build())
                .build();
    }

    private static ScheduledThreadPoolExecutor createTimeoutScheduler() {
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName("graaljs-timeout-scheduler");
            thread.setDaemon(true);
            thread.setContextClassLoader(null);
            return thread;
        });
        scheduler.setRemoveOnCancelPolicy(true);
        scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        return scheduler;
    }

    /** 发布前按 UTF-8 字节再次限制，避免多字节字符突破 32KB 上限。 */
    private static void publishConsole(Value consoleSupport, IGraalJsConsoleListener consoleListener) {
        boolean truncated = false;
        int remainingBytes = MAX_CONSOLE_BYTES;
        try {
            Value logJsonValue = consoleSupport.getMember("readLogs").execute();
            if (logJsonValue != null && logJsonValue.isString()) {
                JSONArray logArray = JSON.parseArray(logJsonValue.asString());
                int publishedCount = 0;
                for (Object item : logArray) {
                    if (publishedCount >= MAX_CONSOLE_COUNT || remainingBytes <= 0) {
                        truncated = true;
                        break;
                    }
                    JSONObject logObject = (JSONObject) item;
                    String message = logObject.getString("message");
                    int messageBytes = message.getBytes(StandardCharsets.UTF_8).length;
                    if (messageBytes > remainingBytes) {
                        message = limitUtf8Bytes(message, remainingBytes);
                        messageBytes = message.getBytes(StandardCharsets.UTF_8).length;
                        truncated = true;
                    }
                    try {
                        consoleListener.onConsole(logObject.getString("level"), message);
                    } catch (Exception ignored) {
                        // 监听器异常不能改变脚本执行结果。
                    }
                    publishedCount++;
                    remainingBytes -= messageBytes;
                }
            }
            Value truncatedValue = consoleSupport.getMember("isTruncated").execute();
            if (truncatedValue != null && truncatedValue.isBoolean() && truncatedValue.asBoolean()) {
                truncated = true;
            }
        } catch (Exception ignored) {
            // 上下文被强制关闭时可能无法读取日志，主执行状态仍以超时为准。
        }
        if (truncated) {
            try {
                consoleListener.onTruncated();
            } catch (Exception ignored) {
                // 监听器异常不能改变脚本执行结果。
            }
        }
    }

    private static GraalJsException diagnosticException(
            GraalJsErrorType errorType,
            PolyglotException exception,
            String originalScript
    ) {
        int originalLineCount = lineCount(originalScript);
        SourceSection sourceSection = exception.getSourceLocation();
        Integer startLine = null;
        Integer startColumn = null;
        Integer endLine = null;
        Integer endColumn = null;
        String sourceSnippet = null;
        if (sourceSection != null) {
            startLine = normalizeLine(sourceSection.getStartLine(), originalLineCount);
            startColumn = sourceSection.getStartColumn();
            endLine = normalizeLine(sourceSection.getEndLine(), originalLineCount);
            endColumn = sourceSection.getEndColumn();
            sourceSnippet = sourceLine(originalScript, startLine);
        }
        return new GraalJsException(
                errorType,
                normalizeMessage(exception.getMessage(), originalLineCount),
                startLine,
                startColumn,
                endLine,
                endColumn,
                sourceSnippet,
                scriptStack(exception, originalLineCount),
                exception
        );
    }

    private static GraalJsException simpleException(GraalJsErrorType errorType, String message, Throwable cause) {
        return new GraalJsException(errorType, message, null, null, null, null, null, null, cause);
    }

    private static int normalizeLine(int wrappedLine, int originalLineCount) {
        int line = wrappedLine - 1;
        if (line < 1) {
            return 1;
        }
        if (line > originalLineCount) {
            return originalLineCount;
        }
        return line;
    }

    private static String normalizeMessage(String message, int originalLineCount) {
        if (message == null) {
            return null;
        }
        String marker = SCRIPT_NAME + ":";
        int markerIndex = message.indexOf(marker);
        if (markerIndex < 0) {
            return message;
        }
        int numberStart = markerIndex + marker.length();
        int numberEnd = numberStart;
        while (numberEnd < message.length() && Character.isDigit(message.charAt(numberEnd))) {
            numberEnd++;
        }
        if (numberEnd == numberStart) {
            return message;
        }
        int wrappedLine = Integer.parseInt(message.substring(numberStart, numberEnd));
        return message.substring(0, numberStart)
                + normalizeLine(wrappedLine, originalLineCount)
                + message.substring(numberEnd);
    }

    private static int lineCount(String script) {
        if (script == null) {
            return 1;
        }
        return script.split("\\R", -1).length;
    }

    private static String sourceLine(String script, Integer line) {
        if (StringUtils.isEmpty(script) || line == null || line < 1) {
            return null;
        }
        String[] lineArray = script.split("\\R", -1);
        if (line > lineArray.length) {
            return null;
        }
        return lineArray[line - 1];
    }

    private static String scriptStack(PolyglotException exception, int originalLineCount) {
        StringBuilder stringBuilder = new StringBuilder();
        for (PolyglotException.StackFrame stackFrame : exception.getPolyglotStackTrace()) {
            if (!stackFrame.isGuestFrame()) {
                continue;
            }
            if (stringBuilder.length() > 0) {
                stringBuilder.append('\n');
            }
            stringBuilder.append("at ");
            if (StringUtils.isNotBlank(stackFrame.getRootName())) {
                stringBuilder.append(stackFrame.getRootName()).append(' ');
            }
            SourceSection sourceSection = stackFrame.getSourceLocation();
            if (sourceSection != null) {
                stringBuilder.append('(')
                        .append(SCRIPT_NAME)
                        .append(':')
                        .append(normalizeLine(sourceSection.getStartLine(), originalLineCount))
                        .append(':')
                        .append(sourceSection.getStartColumn())
                        .append(')');
            }
            if (stringBuilder.length() >= MAX_STACK_CHARS) {
                break;
            }
        }
        if (stringBuilder.length() > MAX_STACK_CHARS) {
            return stringBuilder.substring(0, MAX_STACK_CHARS);
        }
        return stringBuilder.toString();
    }

    private static String limitUtf8Bytes(String source, int maxBytes) {
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

    private static int normalizeTimeout(int timeoutSeconds) {
        if (timeoutSeconds <= 0) {
            return DEFAULT_TIMEOUT_SECONDS;
        } else if (timeoutSeconds > MAX_TIMEOUT_SECONDS) {
            return MAX_TIMEOUT_SECONDS;
        } else {
            return timeoutSeconds;
        }
    }

    private static void closeContext(Context context) {
        try {
            context.close();
        } catch (Exception ignored) {
            // 超时分支已通过 close(true) 关闭上下文，此处只负责正常收尾。
        }
    }
}
