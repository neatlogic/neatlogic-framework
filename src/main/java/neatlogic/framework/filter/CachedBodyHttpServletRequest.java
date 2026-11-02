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

package neatlogic.framework.filter;

import neatlogic.framework.common.config.Config;
import neatlogic.framework.exception.request.RequestBodyException;
import neatlogic.framework.filter.request.SynchronousServletInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.DeferredFileOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/** 认证阶段使用的惰性重放包装；未读 body 时不分配存储，由请求所有者统一关闭。 */
public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(CachedBodyHttpServletRequest.class);
    private final File directory;
    private final int threshold;
    private final List<InputStream> readers = new ArrayList<>();
    private DeferredFileOutputStream storage;
    private boolean initialized;
    private boolean closed;
    private RuntimeException failure;

    /** 保留旧构造入口；调用方仍需管理请求结束时的关闭。 */
    public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
        this(request, Config.REQUEST_BODY_MEMORY_THRESHOLD(), null);
    }

    /** 使用缓存阈值与容器临时目录，不在构造阶段读取上传数据。 */
    public CachedBodyHttpServletRequest(HttpServletRequest request, int threshold, File directory) {
        super(request);
        this.threshold = threshold;
        this.directory = directory;
    }

    /** 首次读时完整快照，保证认证只读前缀后，下游仍能读取完整 body。 */
    public void cacheBody() {
        if (closed) { throw new RequestBodyException(500, "read-closed-cache", getRequestURI(), null); }
        if (failure != null) { throw failure; }
        if (initialized) { return; }
        storage = new DeferredFileOutputStream(threshold, "neatlogic-body-", ".tmp", directory);
        try {
            IOUtils.copyLarge(super.getInputStream(), storage);
            storage.close();
            initialized = true;
        } catch (IOException | RuntimeException ex) {
            logger.error("Cannot cache request body: {}", getRequestURI(), ex);
            // 记录失败后清理未完成的缓存，并立即抛出；保留异常供入口在认证插件吞异常时再次拒绝放行。
            failure = ex instanceof RuntimeException ? (RuntimeException) ex
                    : new RequestBodyException(500, "cache-body", getRequestURI(), ex);
            close();
            throw failure;
        }
    }

    public boolean isCached() { return initialized; }
    public RuntimeException getFailure() { return failure; }

    /** 每个句柄独立重读，获取句柄本身不会触发缓存。 */
    @Override public ServletInputStream getInputStream() {
        return new SynchronousServletInputStream() {
            /** 首次读取初始化缓存，随后从独立句柄重放。 */
            @Override protected InputStream open() throws IOException {
                cacheBody();
                InputStream input = storage.isInMemory() ? new ByteArrayInputStream(storage.getData())
                        : Files.newInputStream(storage.getFile().toPath());
                readers.add(input);
                return input;
            }
        };
    }

    /** Reader 与 InputStream 使用同一份原始字节，避免委托到已经消费的容器流。 */
    @Override public BufferedReader getReader() {
        Charset charset = getCharacterEncoding() == null ? StandardCharsets.ISO_8859_1
                : Charset.forName(getCharacterEncoding());
        return new BufferedReader(new InputStreamReader(getInputStream(), charset));
    }

    /** 关闭所有重放句柄再删除临时文件；清理失败不能覆盖主请求异常。 */
    @Override public void close() {
        if (closed) { return; }
        closed = true;
        for (InputStream reader : readers) {
            try { reader.close(); }
            catch (IOException ex) { logger.error("Cannot close request body reader", ex); }
        }
        if (storage != null) {
            try { storage.close(); }
            catch (IOException ex) { logger.error("Cannot close request body cache", ex); }
            File file = storage.getFile();
            if (file != null) {
                try { Files.deleteIfExists(file.toPath()); }
                catch (IOException ex) { logger.error("Cannot delete request body cache {}", file, ex); }
            }
        }
    }
}
