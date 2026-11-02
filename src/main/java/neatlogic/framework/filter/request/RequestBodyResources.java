/*
 * Copyright (C) 2026  深圳极向量科技有限公司 All Rights Reserved.
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

package neatlogic.framework.filter.request;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.request.RequestBodyException;
import neatlogic.framework.filter.CachedBodyHttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

/** 一个同步请求的资源所有者：统一原始字节限制、可选重放、共享 multipart 和最终清理。 */
public final class RequestBodyResources implements AutoCloseable {
    public static final String ATTRIBUTE = RequestBodyResources.class.getName();
    private static final Logger logger = LoggerFactory.getLogger(RequestBodyResources.class);
    final long multipartMax;
    final int partMemoryThreshold;
    final long fieldsMax;
    final int partsMax;
    final File directory;
    final List<FileItem> items = new ArrayList<>();
    final HttpServletRequest source;
    private final CachedBodyHttpServletRequest cache;
    private final RequestMultipartResolver resolver;
    private final boolean multipart;
    private RequestMultipartResolver.ManagedMultipartRequest parsed;
    private RequestBodyException failure;
    private boolean closed;

    /** 创建时固定当前配置值；原始流仅包装计数，不预读，表单兼容分支保持原有读取顺序。 */
    public RequestBodyResources(HttpServletRequest request, RequestMultipartResolver resolver) {
        int memoryThreshold = Config.REQUEST_BODY_MEMORY_THRESHOLD();
        long bodyMax = Config.REQUEST_BODY_MAX_BYTES();
        multipartMax = Config.REQUEST_MULTIPART_MAX_BYTES();
        partMemoryThreshold = Config.REQUEST_MULTIPART_MEMORY_THRESHOLD();
        fieldsMax = Config.REQUEST_MULTIPART_FIELDS_MAX_BYTES();
        partsMax = Config.REQUEST_MULTIPART_PARTS_MAX();
        this.resolver = resolver;
        multipart = ServletFileUpload.isMultipartContent(request);
        directory = request.getServletContext() == null ? null
                : (File) request.getServletContext().getAttribute("javax.servlet.context.tempdir");
        request.setAttribute(ATTRIBUTE, this);
        source = new LimitedRequest(request, multipart ? multipartMax : bodyMax);
        cache = new CachedBodyHttpServletRequest(source, memoryThreshold, directory);
    }

    /** 未声明结构化上传能力的认证保持旧参数视图，避免未知插件先读参数再读原始流。 */
    public HttpServletRequest forAuthentication(boolean supportsParsedMultipart) {
        if (isFormEncoded()) { cache.cacheBody(); }
        if (!multipart) { return cache; }
        if (supportsParsedMultipart) { return multipartRequest(); }
        return new HttpServletRequestWrapper(cache) {
            /** 上传认证的参数访问保留原始重放，再委托容器，不擅自更改旧参数语义。 */
            private void prepareParameters() { cache.cacheBody(); }
            @Override public String getParameter(String name) { prepareParameters(); return super.getParameter(name); }
            @Override public String[] getParameterValues(String name) { prepareParameters(); return super.getParameterValues(name); }
            @Override public Enumeration<String> getParameterNames() { prepareParameters(); return super.getParameterNames(); }
            @Override public Map<String, String[]> getParameterMap() { prepareParameters(); return super.getParameterMap(); }
        };
    }

    /** 未消费 body 时解除认证缓存包装；上传在路径检查前取得一次共享解析结果。 */
    public HttpServletRequest forDispatch() {
        // 读取、缓存或解析失败时，发生处已经立即抛出异常，并记录失败状态；正常传播时不会执行到这里。
        // 若扩展认证捕获异常后仍返回有效用户，则在放行业务前重新抛出，避免业务读取已消费的残缺 body。
        if (getFailure() != null) { throw getFailure(); }
        if (cache.getFailure() != null) { throw cache.getFailure(); }
        if (multipart) {
            RequestMultipartResolver.ManagedMultipartRequest result = multipartRequest();
            // 普通 JWT 认证只读 Header/Cookie，上传会在这里首次解析；此时解析错误也是首次抛出。
            // 若认证阶段已完成共享解析（如 HMAC 上传），这里只复用结果，不会重复解析。
            result.getMultiFileMap();
            return result;
        }
        return cache.isCached() ? cache : source;
    }

    /** 延迟解析使 HMAC 的请求头及用户检查可以先执行。 */
    private RequestMultipartResolver.ManagedMultipartRequest multipartRequest() {
        if (parsed == null) { parsed = resolver.createManagedRequest(cache.isCached() ? cache : source, this); }
        return parsed;
    }

    /** 请求媒体类型按分号前主类型比较，不受 charset 参数影响。 */
    private boolean isFormEncoded() {
        String type = source.getContentType();
        return type != null && "application/x-www-form-urlencoded".equalsIgnoreCase(type.split(";", 2)[0].trim());
    }

    public RequestBodyException getFailure() {
        if (failure == null && cache.getFailure() instanceof RequestBodyException) {
            failure = (RequestBodyException) cache.getFailure();
        }
        return failure;
    }

    /**
     * 记录请求资源失败状态并返回同一异常，供调用处通过 {@code throw failed(ex)} 立即抛出。
     * 保存异常不代表延迟抛出：它用于在异常被 Spring/业务包装后识别原始错误，
     * 或在认证插件吞掉异常后，由 {@link #forDispatch()} 阻止残缺请求进入业务。
     */
    RequestBodyException failed(RequestBodyException ex) { failure = ex; return ex; }

    /** 入口释放成功解析后接管的字段和文件；解析失败时由 Commons FileUpload 自行清理。 */
    @Override public void close() {
        if (closed) { return; }
        closed = true;
        for (FileItem item : items) {
            try { item.delete(); }
            catch (RuntimeException ex) { logger.error("Cannot delete multipart item for {}", source.getRequestURI(), ex); }
        }
        cache.close();
    }

    /** 透传和缓存共同使用的计数包装，禁止仅依赖 Content-Length。 */
    private final class LimitedRequest extends HttpServletRequestWrapper {
        private final long maximum;
        private ServletInputStream input;
        private long count;

        /** 仅保存限制快照，不初始化容器输入流。 */
        private LimitedRequest(HttpServletRequest request, long maximum) { super(request); this.maximum = maximum; }

        /** 惰性打开容器流，保证头部认证失败时不接收大上传。 */
        @Override public ServletInputStream getInputStream() {
            if (input == null) {
                input = new SynchronousServletInputStream() {
                    /** 首次读取才打开网络流，并检查声明长度。 */
                    @Override protected InputStream open() throws IOException {
                        if (getContentLengthLong() > maximum) {
                            throw failed(new RequestBodyException("body", maximum, getContentLengthLong()));
                        }
                        return new FilterInputStream(LimitedRequest.super.getInputStream()) {
                            /** 多读一个字节用于发现边界，不在拒绝前接收任意大的下一块。 */
                            @Override public int read(byte[] bytes, int offset, int length) throws IOException {
                                if (failure != null) { throw failure; }
                                long remaining = maximum - count;
                                int allowed = remaining < length ? (int) (remaining + 1) : length;
                                int size = in.read(bytes, offset, allowed);
                                account(size);
                                return size;
                            }
                            /** 单字节读取也执行相同字节计数。 */
                            @Override public int read() throws IOException {
                                if (failure != null) { throw failure; }
                                int value = in.read();
                                account(value < 0 ? -1 : 1);
                                return value;
                            }
                            /** 跳过也通过计数读取，避免 skip 绕过大小限制。 */
                            @Override public long skip(long length) throws IOException {
                                byte[] buffer = new byte[8192];
                                long skipped = 0;
                                while (skipped < length) {
                                    int size = read(buffer, 0, (int) Math.min(buffer.length, length - skipped));
                                    if (size < 0) { break; }
                                    skipped += size;
                                }
                                return skipped;
                            }
                        };
                    }
                };
            }
            return input;
        }

        /** 计数只针对从网络读取的原始字节，重放不会重复占用请求额度。 */
        private void account(int size) {
            if (size > 0) {
                count += size;
                if (count > maximum) { throw failed(new RequestBodyException("body", maximum, count)); }
            }
        }

        /** Reader 同样经过计数流，不把读取委托回未受限的容器 Reader。 */
        @Override public BufferedReader getReader() {
            Charset charset = getCharacterEncoding() == null ? StandardCharsets.ISO_8859_1 : Charset.forName(getCharacterEncoding());
            return new BufferedReader(new InputStreamReader(getInputStream(), charset));
        }
    }
}
