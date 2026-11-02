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

import static neatlogic.framework.filter.request.RequestBodyTestSupport.isBodyCached;

import org.apache.commons.io.IOUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import org.slf4j.LoggerFactory;

import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/** 独立受限堆探针，使用生成式输入验证两个并发大文件，不把报文本身放入测试堆。 */
public final class RequestBodyLargeUploadProbe {
    private static final long FILE_SIZE = 128L * 1024 * 1024;

    /** 通过 -Xmx64m 执行；解析后的两个文件同时存在时检查磁盘峰值。 */
    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("neatlogic-body-probe-");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CyclicBarrier barrier = new CyclicBarrier(3);
        try {
            Future<?> first = executor.submit(() -> run(directory, barrier, false));
            Future<?> second = executor.submit(() -> run(directory, barrier, true));
            barrier.await(45, TimeUnit.SECONDS);
            try (Stream<Path> paths = Files.list(directory)) {
                Path[] files = paths.toArray(Path[]::new);
                if (files.length != 2) { throw new AssertionError("Expected exactly two parsed upload files: " + files.length); }
                for (Path file : files) { if (Files.size(file) != FILE_SIZE) { throw new AssertionError("Unexpected temporary file size"); } }
                System.out.println("peakTemporaryFiles=2, peakTemporaryBytes=" + (2 * FILE_SIZE));
            }
            barrier.await(45, TimeUnit.SECONDS);
            first.get(45, TimeUnit.SECONDS); second.get(45, TimeUnit.SECONDS);
            try (Stream<Path> paths = Files.list(directory)) {
                if (paths.findAny().isPresent()) { throw new AssertionError("Temporary files leaked"); }
            }
            System.out.println("bodyCaches=0, uploadBytes=" + (2 * FILE_SIZE) + ", heapLimit=" + Runtime.getRuntime().maxMemory() + ", cleanup=OK");
        } finally {
            executor.shutdownNow();
            executor.awaitTermination(10, TimeUnit.SECONDS);
            try (Stream<Path> paths = Files.list(directory)) {
                for (Path file : paths.toArray(Path[]::new)) { Files.deleteIfExists(file); }
            }
            Files.deleteIfExists(directory);
        }
    }

    /** 普通上传和提前共享解析各走一条并发请求路径，业务重复打开解析后的文件。 */
    private static void run(Path directory, CyclicBarrier barrier, boolean structured) {
        try {
            byte[] prefix = "--probe\r\nContent-Disposition: form-data; name=\"upload\"; filename=\"large.bin\"\r\nContent-Type: application/octet-stream\r\n\r\n".getBytes(StandardCharsets.UTF_8);
            byte[] suffix = "\r\n--probe--\r\n".getBytes(StandardCharsets.UTF_8);
            MockServletContext context = new MockServletContext();
            context.setAttribute("javax.servlet.context.tempdir", directory.toFile());
            MockHttpServletRequest request = new MockHttpServletRequest(context, "POST", "/api/binary/probe") {
                @Override public long getContentLengthLong() { return prefix.length + FILE_SIZE + suffix.length; }
                @Override public int getContentLength() { return (int) getContentLengthLong(); }
                @Override public ServletInputStream getInputStream() {
                    return new SynchronousServletInputStream() {
                        @Override protected InputStream open() {
                            InputStream generated = new InputStream() {
                                private long remaining = FILE_SIZE;
                                @Override public int read() { if (remaining == 0) { return -1; } remaining--; return 0; }
                                @Override public int read(byte[] bytes, int offset, int length) {
                                    if (remaining == 0) { return -1; }
                                    int count = (int) Math.min(remaining, length);
                                    Arrays.fill(bytes, offset, offset + count, (byte) 0); remaining -= count; return count;
                                }
                            };
                            return new SequenceInputStream(Collections.enumeration(Arrays.asList(new ByteArrayInputStream(prefix), generated, new ByteArrayInputStream(suffix))));
                        }
                    };
                }
            };
            request.setContentType("multipart/form-data; boundary=probe");
            try (RequestBodyResources resources = new RequestBodyResources(request, new RequestMultipartResolver())) {
                if (structured) { ((RequestMultipartResolver.ManagedMultipartRequest) resources.forAuthentication(true)).getFormItems(); }
                else { resources.forAuthentication(false).getHeader("Authorization"); }
                MultipartHttpServletRequest parsed = (MultipartHttpServletRequest) resources.forDispatch();
                if (isBodyCached(resources)) { throw new AssertionError("Unexpected body cache"); }
                barrier.await(45, TimeUnit.SECONDS);
                barrier.await(45, TimeUnit.SECONDS);
                for (int i = 0; i < 2; i++) {
                    try (InputStream input = parsed.getFile("upload").getInputStream()) {
                        if (IOUtils.copyLarge(input, OutputStream.nullOutputStream()) != FILE_SIZE) { throw new AssertionError("Truncated upload"); }
                    }
                }
            }
        } catch (Exception ex) { LoggerFactory.getLogger(RequestBodyLargeUploadProbe.class).error("Large upload probe failed", ex); throw new AssertionError(ex); }
    }
}
